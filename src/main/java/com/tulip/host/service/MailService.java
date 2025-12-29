package com.tulip.host.service;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.Builder;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tulip.host.domain.Employee;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import tech.jhipster.config.JHipsterProperties;

/**
 * Service for sending emails.
 * <p>
 * We use the @Async annotation to send emails asynchronously.
 */
@Service
@RequiredArgsConstructor
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JHipsterProperties jHipsterProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final Environment env;

    private final VelocityEngine templateEngine;

    private static final Pattern HTML_TAGS = Pattern.compile("<[^>]+>");

    /**
     * Inner class to represent an email attachment
     */
    @lombok.Getter
    @Builder
    public static class EmailAttachment {
        private final String filename;
        private final byte[] content;

        public EmailAttachment(String filename, byte[] content) {
            this.filename = filename;
            this.content = content;
        }
    }

    @Async
    public void sendEmail(String[] to, String[] cc, String subject, String content, boolean isMultipart, boolean isHtml) {
        sendEmail(to, cc, subject, content, isMultipart, isHtml, null);
    }

    @Async
    public void sendEmail(String[] to, String[] cc, String subject, String content, boolean isMultipart, boolean isHtml, List<EmailAttachment> attachments) {
        subject = appendEnvToSubjectIfNotProd(subject);
        log.debug(
                "Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                isMultipart,
                isHtml,
                to,
                subject,
                content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            // If we send HTML, prefer multipart/alternative with a text fallback (improves
            // rendering in clients like Gmail).
            boolean multipart = isMultipart || isHtml || (attachments != null && !attachments.isEmpty());
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, multipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setCc(cc);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            if (isHtml) {
                message.setText(toPlainText(content), content);
            } else {
                message.setText(content, false);
            }

            // Add attachments if provided
            if (attachments != null && !attachments.isEmpty()) {
                for (EmailAttachment attachment : attachments) {
                    message.addAttachment(attachment.getFilename(), new ByteArrayResource(attachment.getContent()));
                }
            }

            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User");
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.warn("Email could not be sent to user '{}'", to, e);
            } else {
                log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
            }
        }
    }

    private String toPlainText(String html) {
        if (html == null) {
            return "";
        }
        String s = html;
        s = s.replace("<br/>", "\n").replace("<br />", "\n").replace("<br>", "\n");
        s = s.replace("</p>", "\n\n");
        s = HTML_TAGS.matcher(s).replaceAll("");
        return s.trim();
    }

    public void sendEmailFromTemplate(
            String[] to,
            String[] cc,
            String templateName,
            String titleKey,
            Map<String, Object> variables,
            Locale locale,
            boolean isMultipart) {
        sendEmailFromTemplate(to, cc, templateName, titleKey, variables, locale, isMultipart, null);
    }

    public void sendEmailFromTemplate(
            String[] to,
            String[] cc,
            String templateName,
            String titleKey,
            Map<String, Object> variables,
            Locale locale,
            boolean isMultipart,
            List<EmailAttachment> attachments) {
        String subject = messageSource.getMessage(titleKey, null, locale);
        String content = renderTemplate(templateName, variables);
        sendEmail(to, cc, subject, content, isMultipart, true, attachments);
    }

    private String appendEnvToSubjectIfNotProd(String subject) {
       String label = !env.getDefaultProfiles()[0].equalsIgnoreCase("prod") ? env.getDefaultProfiles()[0].toUpperCase() : "";
        return "[" + label + "] " + subject;
    }

    public String renderTemplate(String templateName, Map<String, Object> variables) {
        try {
            Template template = templateEngine.getTemplate(templateName, StandardCharsets.UTF_8.name());
            VelocityContext context = new VelocityContext(variables);
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            return writer.toString();
        } catch (Exception e) {
            log.warn("Could not render email template '{}': {}", templateName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Convenience overload: uses a {@code user} variable in the template model.
     */
    @Async
    public void sendEmailFromTemplate(
            String[] to,
            String[] cc,
            Employee user,
            String templateName,
            String titleKey,
            Locale locale,
            boolean isMultipart) {
        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        sendEmailFromTemplate(to, cc, templateName, titleKey, model, locale, isMultipart, null);
    }

    /**
     * Convenience overload: uses a {@code user} variable in the template model with attachments.
     */
    @Async
    public void sendEmailFromTemplate(
            String[] to,
            String[] cc,
            Employee user,
            String templateName,
            String titleKey,
            Locale locale,
            boolean isMultipart,
            List<EmailAttachment> attachments) {
        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        sendEmailFromTemplate(to, cc, templateName, titleKey, model, locale, isMultipart, attachments);
    }

}

