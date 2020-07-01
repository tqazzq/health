package test;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.health.utils.SMSUtils;

/**
 * @Author Tian Qing
 * @Daate: Created in 23:10 2020/6/29
 */
public class SendMessage {
    public static void main(String[] args) throws ClientException {
        SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,"18083319113","123456");
    }
}
