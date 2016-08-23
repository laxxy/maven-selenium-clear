package com.clear.selenium.utils;


import org.testng.Assert;
import ru.yandex.qatools.allure.annotations.Step;

import javax.mail.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mail {
    private static Folder openedFolder;
    private static Store store;
    public static int ELEMENT_EXTRALONG_TIMEOUT_SECONDS = 120;

    @Step
    private static void connect(String login, String password) throws MessagingException {
        Matcher matcher = Pattern.compile(".(\\+[0-9a-zA-Z]+)@").matcher(login);
        String trueLogin = matcher.find() ? login.replace(matcher.group(1), "") : login;

        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(props, null);
        store = session.getStore("imaps");
        store.connect("imap.gmail.com", trueLogin, password);
    }

    @Step
    private static void disconnect() throws MessagingException {
        if (openedFolder != null) {
            openedFolder.close(true);
            openedFolder = null;
        }

        if (store != null) {
            store.close();
            store = null;
        }
    }

    @Step
    private static List<Message> getUnreadMessages(String folderName) throws MessagingException {
        List<Message> unreadMessages = new ArrayList<>();
        if (store != null) {
            openedFolder = store.getFolder(folderName);
            openedFolder.open(Folder.READ_WRITE);
            Message messages[] = openedFolder.getMessages();

            for (Message msg : messages) {
                if (!msg.isSet(Flags.Flag.SEEN)) {
                    unreadMessages.add(msg);
                }
            }
        }

        return unreadMessages;
    }

    @Step
    public static List<EmailMessage> getEmailMessages(String email, String password, String expectedSubject) {
        return getEmailMessages(email, password, "Inbox", expectedSubject);
    }

    @Step
    public static List<EmailMessage> waitForEmail(String email, String password, String expectedSubject) {
        long startTime = System.currentTimeMillis();
        List<EmailMessage> messages = getEmailMessages(email, password, expectedSubject);
        while (messages.size() == 0 && System.currentTimeMillis() - startTime < ELEMENT_EXTRALONG_TIMEOUT_SECONDS * 2000) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            messages = getEmailMessages(email, password, expectedSubject);
        }
        return messages;
    }

    @Step
    public static List<EmailMessage> waitForEmail(String email, String password, String folder, String expectedSubject, String expectedBody) {
        long startTime = System.currentTimeMillis();
        List<EmailMessage> messages = getEmailMessages(email, password, folder, expectedSubject, expectedBody);
        while (messages.size() == 0 && System.currentTimeMillis() - startTime < ELEMENT_EXTRALONG_TIMEOUT_SECONDS * 2000) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            messages = getEmailMessages(email, password, folder, expectedSubject, expectedBody);
        }
        return messages;
    }


    public static List<EmailMessage> getEmailMessages(String login, String password, String folder, String expectedSubject) {
        return getEmailMessages(login, password, folder, expectedSubject, "Hello User+");
    }

    @Step
    public static List<EmailMessage> getEmailMessages(String login, String password, String folder, String expectedSubject, String expectedBody) {
        List<EmailMessage> ret = new ArrayList<>();

        try {
            connect(login, password);

            List<Message> messages = getUnreadMessages(folder);

            for (Message msg : messages) {
                String from = "unknown";
                if (msg.getReplyTo().length >= 1) {
                    from = msg.getReplyTo()[0].toString();
                } else if (msg.getFrom().length >= 1) {
                    from = msg.getFrom()[0].toString();
                }

                String subject = msg.getSubject();
                Object content = msg.getContent();
                String body = "";
                if (content instanceof Multipart) {
                    int parts = ((Multipart) content).getCount();
                    for (int i = 0; i < parts; i++) {
                        body += ((Multipart) content).getBodyPart(i).getContent();
                    }
                } else {
                    body = (String) content;
                }

                EmailMessage email = new EmailMessage();
                email.setFrom(from);
                email.setBody(body);
                email.setSubject(subject);

                if (expectedSubject != null && !msg.getSubject().contains(expectedSubject)) {
                    msg.setFlag(Flags.Flag.SEEN, false);
                }
                if (expectedSubject != null && msg.getSubject().contains(expectedSubject)) {
                    if (expectedBody == null || email.getBody().contains(expectedBody)) {
                        ret.add(email);
                    }
                } else if (expectedSubject == null) {
                    ret.add(email);
                }
            }
        } catch (NoSuchProviderException nspe) {
            nspe.printStackTrace();
        } catch (MessagingException mse) {
            mse.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {

            try {
                disconnect();
            } catch (MessagingException mse) {
                mse.printStackTrace();
            }
        }
        return ret;
    }

    @Step
    public static void clearInboxFolder(String login, String password) {
        clearFolder(login, password, "Inbox");
    }

    @Step
    public static void clearFolder(String login, String password, String folderName) {
        try {
            connect(login, password);

            openedFolder = store.getFolder(folderName);
            openedFolder.open(Folder.READ_WRITE);
            Message messages[] = openedFolder.getMessages();

            for (Message msg : messages)
                msg.setFlag(Flags.Flag.DELETED, true);

        } catch (NoSuchProviderException nspe) {
            nspe.printStackTrace();
        } catch (MessagingException mse) {
            mse.printStackTrace();
        } finally {

            try {
                disconnect();
            } catch (MessagingException mse) {
                mse.printStackTrace();
            }
        }

    }

    @Step
    public static String getLink(String email, String password, String folder, String expectedSubject, String expectedBody) {
        List<EmailMessage> emails = Mail.waitForEmail(email, password, folder, expectedSubject, expectedBody);
        Assert.assertTrue(!emails.isEmpty(), "Could not get confirmation letter!");

        String link = null;

        for (EmailMessage message : emails) {
            Pattern linkPattern = Pattern.compile("click the button below.*a href=[\"']([^']*?)[\"']");
            Matcher matcher = linkPattern.matcher(message.getBody());

            if (matcher.find()) {
                link = matcher.group(1);
                break;
            }

        }

        return link;
    }

}
