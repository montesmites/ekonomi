package se.montesmites.ekonomi.report;

import java.util.function.UnaryOperator;

public interface AccountGroup {

  static AccountGroup of(String description, String regex) {
    return new AccountGroup() {
      @Override
      public String description() {
        return description;
      }

      @Override
      public String regex() {
        return regex;
      }
    };
  }

  String description();

  String regex();

  default UnaryOperator<AmountsProvider> postProcessor() {
    return row -> row;
  }

  default AccountGroup postProcessor(UnaryOperator<AmountsProvider> postProcessor) {
    var base = this;
    return new AccountGroup() {
      @Override
      public String description() {
        return base.description();
      }

      @Override
      public String regex() {
        return base.regex();
      }

      @Override
      public UnaryOperator<AmountsProvider> postProcessor() {
        return postProcessor;
      }
    };
  }
}
