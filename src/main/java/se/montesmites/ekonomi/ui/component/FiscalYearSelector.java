package se.montesmites.ekonomi.ui.component;

import static java.util.Comparator.comparing;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import se.montesmites.ekonomi.db.FiscalYearData;
import se.montesmites.ekonomi.i18n.Dictionary;
import se.montesmites.ekonomi.i18n.Translator;
import se.montesmites.ekonomi.service.FiscalYearService;
import se.montesmites.ekonomi.session.SessionAccessor;

public class FiscalYearSelector extends HorizontalLayout implements Translator {

  public FiscalYearSelector(FiscalYearService fiscalYearService, SessionAccessor sessionAccessor) {
    var years =
        fiscalYearService.findAll().stream()
            .sorted(comparing(FiscalYearData::calendarYear).reversed())
            .toList();
    var latest = years.stream().max(comparing(FiscalYearData::calendarYear));
    var selected = sessionAccessor.fiscalYear().or(() -> latest);

    var fiscalYearCombo = new ComboBox<FiscalYearData>(t(Dictionary.FISCAL_YEAR));
    fiscalYearCombo.setItemLabelGenerator(
        year -> year.calendarYear().format(DateTimeFormatter.ofPattern("yyyy")));
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
