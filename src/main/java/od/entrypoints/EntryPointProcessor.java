package od.entrypoints;

import javax.servlet.http.HttpServletRequest;

public interface EntryPointProcessor {
  String post(HttpServletRequest request, String tenantId);
  String get(HttpServletRequest request, String tenantId);
}
