package br.com.devcansado.multitenant.db.config.request;

import br.com.devcansado.multitenant.db.config.TenantIdentifierResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

  private static final String TENANT_HEADER = "X-Tenant-ID";

  @Autowired
  TenantIdentifierResolver currentTenant;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String tenant = request.getHeader(TENANT_HEADER);

    if (tenant == null || tenant.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("Tenant ID is missing in the request headers");
      return false;
    }

    currentTenant.setCurrentTenant(tenant); // Define tenant context
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    currentTenant.setCurrentTenant("unknown"); // Remove context for request
  }
}