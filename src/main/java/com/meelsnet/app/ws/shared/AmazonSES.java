package com.meelsnet.app.ws.shared;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.meelsnet.app.ws.shared.dto.UserDto;

public class AmazonSES {
    // Verified address in Amazon SES
    final String FROM = "rick.meels@gmail.com";

    // Subject for email
    final String NEW_USER_REGISTRATION_SUBJECT = "One last step to complete your registration with PhotoApp";
    final String PASSWORD_RESET_SUBJECT = "Password reset request";

    // Body of email in HTML
    final String NEW_USER_REGISTRATION_HTMLBODY = "<h1>Please verify your email address</h1>" +
            "<p>Thank you for registering with our mobile app. To complete registration process and be able to log in," +
            "click on the following link: " +
            "<a href='http://localhost:8080/verification-service/email-verification.html?token=$tokenValue'>" +
            "Final step to complete your registration" +
            "</a><br/><br/>" +
            "Thank you! And we are waiting for you inside!";
    final String PASSWORD_RESET_HTMLBODY = "<h1>A request to reset your password</h1>" +
            "<p>Hi, $firstName!</p> " +
            "<p>Someone has requested to reset your password with our project. If it were not you, please ignore it." +
            " otherwise please click on the link below to set a new password: " +
            "<a href='http://localhost:8080/verification-service/password-reset.html?token=$tokenValue'>" +
            " Click this link to Reset Password" +
            "</a><br/><br/>" +
            "Thank you!";

    // Body of email in plain text
    final String NEW_USER_REGISTRATION_TEXTBODY = "Please verify your email address." +
            "Thank you for registering with our mobile app. To complete registration process and be able to log in," +
            "click on the following link: " +
            "http://localhost:8080/verification-service/email-verification.html?token=$tokenValue" +
            "Thank you! And we are waiting for you inside!";
    final String PASSWORD_RESET_TEXT_BODY = "Hi, $firstName" +
            "Someone has requested to reset your password with our project. If it were not you, please ignore it." +
            "otherwise please open the link below in your browser window to set a new password:" +
            "http://localhost:8080/verification-service/password-reset.html?token=$tokenValue" +
            "Thank you!";

    public void verifyEmail(UserDto userDto) {
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                .withRegion(Regions.EU_WEST_3).build();

        String htmlBodyWithToken = NEW_USER_REGISTRATION_HTMLBODY.replace("$tokenValue", userDto.getEmailVerificationToken());
        String textBodyWithToken = NEW_USER_REGISTRATION_TEXTBODY.replace("$tokenValue", userDto.getEmailVerificationToken());

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userDto.getEmail()))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content().withCharset("UTF-8").withData(NEW_USER_REGISTRATION_SUBJECT)))
                .withSource(FROM);

        client.sendEmail(request);

        System.out.println("Email sent!");
    }

    public boolean sendPasswordResetRequest(String firstName, String email, String token) {
        boolean returnValue = false;

        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.EU_WEST_3).build();

        String htmlBodyWithToken = PASSWORD_RESET_HTMLBODY.replace("$tokenValue", token);
        htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", firstName);

        String textBodyWithToken = PASSWORD_RESET_TEXT_BODY.replace("$tokenValue", token);
        textBodyWithToken = PASSWORD_RESET_TEXT_BODY.replace("$firstName", firstName);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(PASSWORD_RESET_SUBJECT)))
                .withSource(FROM);

        SendEmailResult result = client.sendEmail(request);
        if (result != null && (result.getMessageId() != null && !result.getMessageId().isEmpty())) {
            returnValue = true;
        }

        return returnValue;
    }
}
