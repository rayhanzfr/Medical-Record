package com.prodia.technical.logging.listener;

import com.prodia.technical.authentication.helper.SessionHelper;
import com.prodia.technical.authentication.model.UserPrincipal;
import com.prodia.technical.authentication.persistence.entity.User;
import com.prodia.technical.logging.event.EntityAuditTrailEvent;
import com.prodia.technical.logging.event.EntityAuditTrailEvent.Action;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
@NoArgsConstructor
@Component
public class AuditTrailListener {

  @Autowired
  private ApplicationEventPublisher applicationEventPublisher;

  @PostPersist
  private void onInsert(Object entity) {
    applicationEventPublisher.publishEvent(createEvent(entity, Action.CREATE));
  }

  @PostUpdate
  private void onUpdate(Object entity) {
    applicationEventPublisher.publishEvent(createEvent(entity, Action.UPDATE));
  }

  @PostRemove
  private void onDelete(Object entity) {
    applicationEventPublisher.publishEvent(createEvent(entity, Action.DELETE));
  }

  private EntityAuditTrailEvent createEvent(Object entity, Action action) {
    var event = new EntityAuditTrailEvent(this);
    event.setEntity(entity);
    event.setAction(action);
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      event.setTenantId(null);
      try {
        event.setUser(InetAddress.getLocalHost().getHostAddress());
      } catch (UnknownHostException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown host.");
      }
    } else {
      UserPrincipal userPrinciple = SessionHelper.getUserPrincipal();
      User user = userPrinciple.getUser();
      if (user != null) {
        event.setTenantId(null);
        event.setUser(user.getUsername());
      } else {
        event.setTenantId(null);
        event.setUser(null);
      }
    }
    return event;
  }

}