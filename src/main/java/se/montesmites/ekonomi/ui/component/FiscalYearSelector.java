package se.montesmites.ekonomi.ui.component;

import static java.util.Comparator.comparing;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.service.FiscalYearService;

public class FiscalYearSelector extends HorizontalLayout implements Translator {

  public FiscalYearSelector(FiscalYearService fiscalYearService) {
    var years =
        fiscalYearService.findAll().stream().sorted(comparing(Year::year).reversed()).toList();
    var latest = years.stream().max(comparing(Year::year));

    var fiscalYearCombo = new ComboBox<Year>(t(Dictionary.FISCAL_YEAR));
    fiscalYearCombo.setItemLabelGenerator(year -> String.valueOf(year.year().getValue()));
    fiscalYearCombo.setItems(years);
    latest.ifPresent(fiscalYearCombo::setValue);

    fiscalYearCombo.addValueChangeListener(
        changeEvent -> {
          Notification.show(changeEvent.getValue().toString());
        });

    add(fiscalYearCombo);
  }
}
