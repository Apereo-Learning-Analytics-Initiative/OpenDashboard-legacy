package org.apereo.lai;

import java.util.List;

import org.apereo.lai.impl.ResultImpl;

public interface LineItem {

  String getType();

  String getTitle();

  String getContext();

  Double getMaximumScore();

  List<ResultImpl> getResults();

}