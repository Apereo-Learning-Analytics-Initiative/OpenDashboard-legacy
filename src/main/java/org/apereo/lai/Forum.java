package org.apereo.lai;

import java.util.List;

import org.apereo.lai.impl.TopicImpl;

public interface Forum {

  String getTitle();

  List<TopicImpl> getTopics();

}