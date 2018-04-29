package se.montesmites.ekonomi.report.xml;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.function.Function;

public class XmlAccountGroupRef implements XmlAccountGroupSupplier {
    private String id;

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public XmlAccountGroup get(Function<String, XmlAccountGroup> accountGroups) {
        return accountGroups.apply(id);
    }
}
