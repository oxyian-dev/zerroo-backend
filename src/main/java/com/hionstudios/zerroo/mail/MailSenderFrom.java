package com.hionstudios.zerroo.mail;

public class MailSenderFrom {
    private final String email;
    private final String name;

    public MailSenderFrom(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public static MailSenderFrom noReply() {
        return new MailSenderFrom("no-reply@zerroo.in", "Zerroo");
    }
}
