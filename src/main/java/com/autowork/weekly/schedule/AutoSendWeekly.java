package com.autowork.weekly.schedule;

import com.alibaba.fastjson.JSON;
import com.autowork.weekly.dao.ClassInfoDao;
import com.autowork.weekly.dao.MailInfoDao;
import com.autowork.weekly.dao.MailInfoLogDao;
import com.autowork.weekly.dao.ProfileDao;
import com.autowork.weekly.domain.ClassInfo;
import com.autowork.weekly.domain.MailInfo;
import com.autowork.weekly.domain.MailInfoLog;
import com.autowork.weekly.domain.Profile;
import com.autowork.weekly.domain.emun.ProfileName;
import com.autowork.weekly.domain.emun.ProjectType;
import com.autowork.weekly.service.FileReadService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Component
public class AutoSendWeekly {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${mail.fromMail.sender}")
    private String sender;

    @Value("${mail.fromMail.receiver}")
    private String receiver;

    @Value("${mail.fromMail.receiverMaster}")
    private String receiverMaster;

    @Value("${path.class}")
    private String pathClass;

    @Value("${path.packageName}")
    private String pathPackageName;

    @Value("${mail.fromMail.subject}")
    private String mailSubjectValue;


    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private FileReadService fileReadService;

    @Resource
    private MailInfoDao mailInfoDao;

    @Resource
    private ClassInfoDao classInfoDao;

    @Resource
    private MailInfoLogDao mailInfoLogDao;

    @Resource
    private ProfileDao profileDao;


    /**
     * 定时发送周报
     */
    @Scheduled(cron="0 3 2 ? * 3")
    public void sendWeeklyMail(){
        boolean schBoolean = true;
        String schConent = "";
        if(!this.getSwith(ProfileName.SCHEDULE_SWITCH)){
            schBoolean = false;
            logger.info("周报定时开关关闭，不需要发送邮件给领导");
            return;
        }

        try{
            //先获取上一次存入的类信息
            ClassInfo classInfoLastOne = this.classInfoDao.selectLastOne(ProjectType.OP_ADMIN.getId());
            //获取本次类信息
            ClassInfo classInfoNow = this.weeklyBefore();

            if(classInfoLastOne == null){
                return;
            }

            //对比信息获取邮件正文
            StringBuffer content = new StringBuffer();
            content.append("<html>\n <body>\n");
            content.append(this.different(classInfoLastOne,classInfoNow));

            logger.info(content.toString());

            //查询数据库中有没有需要补充的数据
            List<MailInfo> mailInfoList = this.mailInfoDao.select(ProjectType.OP_ADMIN.getId());
            if(mailInfoList != null){
                for(MailInfo mailInfo : mailInfoList){
                    content.append("<h3>");
                    content.append(mailInfo.getMailContent());
                    content.append(";</h3>");
                }
            }
            this.mailInfoDao.delete(ProjectType.OP_ADMIN.getId());
            content.append("</body>\n </html>");
            schConent = content.toString();
            logger.info(schConent);
            //发送邮件
            this.sendMail(content.toString(),mailSubjectValue,receiver);

            try{
                MailInfoLog mailInfoLog = new MailInfoLog();
                Long sysTime = System.currentTimeMillis()/1000;
                mailInfoLog.setProjectType(ProjectType.OP_ADMIN.getId());
                mailInfoLog.setMailSubject(mailSubjectValue);
                mailInfoLog.setMailContent(content.toString());
                mailInfoLog.setCreateTime(sysTime);
                mailInfoLog.setUpdateTime(sysTime);
                this.mailInfoLogDao.insert(mailInfoLog);
            }catch (Exception e){
                logger.error("存储邮件信息出错",e);
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            schConent = e.getMessage();
            schBoolean = false;
        }finally {
            try {
                if(schBoolean){
                    this.sendMail(schConent,"定时发送邮件成功了",receiverMaster);
                }else{
                    this.sendMail(schConent,"定时发送邮件失败了",receiverMaster);
                }
            }catch (Exception e){
                logger.error(e.getMessage(),e);
            }
        }
    }


    /**
     * 发邮件前去取一边项目信息
     * @return
     */
    public ClassInfo weeklyBefore() throws Exception {
        Set<Class<?>> set = new HashSet<>();
        Map<String,String> result = new HashMap<String,String>();
        result = this.fileReadService.findClassAndToMap(pathPackageName,pathClass+classNameToPath("."+pathPackageName),true,set);

        //获取map后存入数据库
        ClassInfo classInfo = new ClassInfo();
        classInfo.setProjectType(ProjectType.OP_ADMIN.getId());
        classInfo.setClassMessage(JSON.toJSONString(result));
        Long sysTime = System.currentTimeMillis()/1000;
        classInfo.setCreateTime(sysTime);
        classInfo.setUpdateTime(sysTime);
        this.classInfoDao.insert(classInfo);
        set = null;
        result = null;
        return classInfo;
    }


    /**
     * 对比这次以及上次数据，生成邮件正文
     * @param lastClassInfo
     * @param nowClassInfo
     * @return
     */
    private String different(ClassInfo lastClassInfo,ClassInfo nowClassInfo) {
        String classMessageLast = lastClassInfo.getClassMessage();
        Map<String, String> mapLast = (Map<String, String>) JSON.parse(classMessageLast);
        String classMessageNow = nowClassInfo.getClassMessage();
        Map<String, String> mapNow = (Map<String, String>) JSON.parse(classMessageNow);
        StringBuffer sb = new StringBuffer();

        if (mapLast != null && !mapLast.isEmpty() && mapNow != null && !mapNow.isEmpty()) {
            Iterator<Map.Entry<String, String>> entrieNow = mapNow.entrySet().iterator();

            while (entrieNow.hasNext()) {
                Map.Entry<String, String> entryNow = entrieNow.next();
                String strNow = mapNow.get(entryNow.getKey());
                String strLast = mapLast.get(entryNow.getKey());
                String str[] = entryNow.getKey().split("\\.");
                if (strLast == null) {
                    //新增xx页面

                    sb.append("<h3>新增" + str[str.length-1] + "页面;</h3>\n");
                } else {
                    boolean addBoolean = true;
                    sb.append("<h3>");
                    sb.append(str[str.length-1] + "页面");
                    List<String> listLast = JSON.parseArray(strLast, String.class);
                    List<String> listNow = JSON.parseArray(strNow, String.class);
                    for (int i = 0; i < listNow.size(); i++) {
                        if (!listLast.contains(listNow.get(i))) {
                            //新增xx字段
                            sb.append("新增了" + listNow.get(i) + "字段,");
                            addBoolean  =false;
                        }
                    }

                    for (int i = 0; i < listLast.size(); i++) {
                        if (!listNow.contains(listLast.get(i))) {
                            //删除了xx字段
                            sb.append("删除了" + listNow.get(i) + "字段,");
                            addBoolean = false;
                        }
                    }
                    if(addBoolean){
                       sb.delete(sb.length()-(6+str[str.length-1].length()),sb.length());
                    }else{
                        sb.deleteCharAt(sb.length()-1);
                        sb.append(";</h3>\n");
                    }
                }
            }

            Iterator<Map.Entry<String, String>> entrieLast = mapLast.entrySet().iterator();

            while (entrieLast.hasNext()) {
                Map.Entry<String, String> entryLast = entrieLast.next();
                String strNow = mapNow.get(entryLast.getKey());
                if (strNow == null) {
                    //删除xx页面
                    String str[] = entryLast.getKey().split("\\.");
                    sb.append("<h3>");
                    sb.append("删除" + str[str.length-1] + "页面;</h3>\n");
                }
            }
        }
        return sb.toString();
    }


    /**
     * 查询是否需要发送周报
     * @param name
     * @return
     */
    public boolean getSwith(String name){
        Profile profile = this.profileDao.select(name);
        if(profile != null && StringUtils.isNotBlank(profile.getContent())){
            if("1".equals(profile.getContent())){
                return true;
            }
        }
        return false;
    }

    /**
     * send mail
     * @param content
     * @param subject
     * @return
     */
    public void sendMail(String content,String subject,String receiverS) throws Exception{
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(sender);
        helper.setTo(receiverS);
        helper.setSubject(subject);
        helper.setText(content, true);

        javaMailSender.send(message);
        logger.info("mail send success,receiverS={}",receiverS);
    }

    private String classNameToPath(String name) {
        return   name.replace(".", "/");
    }

}
