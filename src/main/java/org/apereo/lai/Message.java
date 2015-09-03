package org.apereo.lai;

public interface Message {

  String getAuthor();

  String getTitle();

  String getCreated();

  String getReplyTo();

}