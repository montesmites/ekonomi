package se.montesmites.ekonomi.organization;

import java.util.ArrayList;
import java.util.List;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.Year;

class OrganizationBuilder {

    private List<Year> years;
    private List<Event> events;

    public OrganizationBuilder() {
        this.years = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    public void setYears(List<Year> years) {
        this.years = years;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Organization build() {
        Organization o = new Organization(years, events);
        return o;
    }
}
