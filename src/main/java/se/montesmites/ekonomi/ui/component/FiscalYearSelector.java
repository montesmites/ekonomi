package se.montesmites.ekonomi.ui.component;

import static java.util.Comparator.comparing;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.util.Optional;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
import se.montesmites.ekonomi.model.Year;
import se.montesmites.ekonomi.service.FiscalYearService;
import se.montesmites.ekonomi.session.SessionAccessor;

public class FiscalYearSelector extends HorizontalLayout implements Translator {

  public FiscalYearSelector(FiscalYearService fiscalYearService, SessionAccessor sessionAccessor) {
    var years =
        fiscalYearService.findAll().stream().sorted(comparing(Year::year).reversed()).toList();
    var latest = years.stream().max(comparing(Year::year));
    var selected = sessionAccessor.fiscalYear().or(() -> latest);

    var fiscalYearCombo = new ComboBox<Year>(t(Dictionary.FISCAL_YEAR));
    fiscalYearCombo.setItemLabelGenerator(year -> String.valueOf(year.year().getValue()));
    fiscalYearCombo.setItems(years);
    selected.ifPresent(
        value -> {
          fiscalYearCombo.setValue(value);
          sessionAccessor.mutate(sessionData -> sessionData.withFiscalYear(Optional.of(value)));
        });

    fiscalYearCombo.addValueChangeListener(
        changeEvent -> {
          sessionAccessor.mutate(
              sessionData ->
                  sessionData.withFiscalYear(Optional.ofNullable(changeEvent.getValue())));
          UI.getCurrent().getPage().reload();
        });

    add(fiscalYearCombo);
  }
}
