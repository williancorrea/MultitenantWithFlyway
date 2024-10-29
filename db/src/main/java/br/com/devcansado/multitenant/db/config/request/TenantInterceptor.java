package br.com.devcansado.multitenant.db.config.request;

import br.com.devcansado.multitenant.db.config.properties.PropertiesTenantNames;
import br.com.devcansado.multitenant.db.config.tenant.TenantIdentifierResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

  private static final String TENANT_HEADER = "X-Tenant-ID";

  private final TenantIdentifierResolver currentTenant;
  private final PropertiesTenantNames propertiesTenantNames;

  public TenantInterceptor(TenantIdentifierResolver currentTenant,
                           PropertiesTenantNames propertiesTenantNames) {
    this.currentTenant = currentTenant;
    this.propertiesTenantNames = propertiesTenantNames;
  }

  @Override
  public boolean preHandle(HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler)
      throws Exception {
    
    if("OPTIONS".equalsIgnoreCase(request.getMethod())){
      return true;
    }
    
    String tenant = request.getHeader(TENANT_HEADER);

    if (tenant == null || tenant.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("Tenant ID is missing in the request headers (X-Tenant-ID: XXX)");
      return false;
    }

    if (!propertiesTenantNames.getTenants().contains(tenant)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("Tenant ID is invalid: " + tenant);
      return false;
    }

    currentTenant.setCurrentTenant(tenant); // Define tenant context
    return true;
  }

  @Override
  public void afterCompletion(@NonNull HttpServletRequest request,
                              @NonNull HttpServletResponse response,
                              @NonNull Object handler, Exception ex) {
    currentTenant.setCurrentTenant("unknown"); // Remove context for request
  }
}