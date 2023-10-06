package se.montesmites.ekonomi.session;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import java.util.Optional;
import java.util.function.UnaryOperator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import se.montesmites.ekonomi.db.FiscalYearData;

@Component
@AllArgsConstructor
public class SessionAccessor {

  private static final String SESSION_DATA_ATTRIBUTE_NAME = "SESSION_DATA";

  private Optional<WrappedSession> session() {
    return Optional.ofNullable(VaadinSession.getCurrent())
        .flatMap(vaadinSession -> Optional.ofNullable(vaadinSession.getSession()));
  }

  private Optional<SessionData> sessionData() {
    return this.session()
        .map(session -> session.getAttribute(SESSION_DATA_ATTRIBUTE_NAME))
        .flatMap(
            sessionData ->
                switch (sessionData) {
                  case SessionData _sessionData -> Optional.of(_sessionData);
                  default -> Optional.empty();
                });
  }

  private SessionAccessor save(SessionData sessionData) {
    this.session()
        .ifPresent(session -> session.setAttribute(SESSION_DATA_ATTRIBUTE_NAME, sessionData));
    return this;
  }

  public SessionAccessor mutate(UnaryOperator<SessionData> mutator) {
    var sessionData = this.sessionData().orElse(new SessionData());
    return this.save(mutator.apply(sessionData));
  }

  public Optional<FiscalYearData> fiscalYear() {
    return this.sessionData().flatMap(SessionData::fiscalYear);
  }
}
