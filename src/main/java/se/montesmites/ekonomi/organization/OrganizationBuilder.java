package se.montesmites.ekonomi.organization;

import java.util.ArrayList;
import java.util.List;
import se.montesmites.ekonomi.model.Event;

class OrganizationBuilder {

    private List<Event> events;

    public OrganizationBuilder() {
        this.events = new ArrayList<>();
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
    
    public Organization build() {
        Organization o = new Organization(events);
        return o;
    }
}
