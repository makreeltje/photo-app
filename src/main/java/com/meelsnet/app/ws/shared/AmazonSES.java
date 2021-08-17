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
    final String SUBJECT = "One last step to complete your registration with PhotoApp";
    // Body of email in HTML
    final String HTMLBODY = "<h1>Please verify your email address</h1>" +
            "<p>Thank you for registering with our mobile app. To complete registration process and be able to log in," +
            "click on the following link: " +
            "<a href='http://ec2-3-9-165-213.eu-west-2.compute.amazonaws.com:8080/verification-service/email-verification.html?token=$tokenValue'>" +
            "Final step to complete your registration" +
            "</a><br/><br/>" +
            "Thank you! And we are waiting for you inside!";
    // Body of email in plain text
    final String TEXTBODY = "Please verify your email address." +
            "Thank you for registering with our mobile app. To complete registration process and be able to log in," +
            "click on the following link: " +
            "http://ec2-3-9-165-213.eu-west-2.compute.amazonaws.com:8080/verification-service/email-verification.html?token=$tokenValue" +
            "Thank you! And we are waiting for you inside!";

    public void verifyEmail(UserDto userDto) {
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                .withRegion(Regions.EU_WEST_3).build();

        String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", userDto.getEmailVerificationToken());
        String textBodyWithToken = TEXTBODY.replace("$tokenValue", userDto.getEmailVerificationToken());

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userDto.getEmail()))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
                .withSource(FROM);

        client.sendEmail(request);

        System.out.println("Email sent!");
    }
}
