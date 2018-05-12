/** Created on 25/02/18. */
module com.cosium.vet {
  opens com.cosium.vet;
  opens com.cosium.vet.gerrit;
  opens com.cosium.vet.gerrit.config;
  opens com.cosium.vet.log;
  opens com.cosium.vet.git;
  opens com.cosium.vet.runtime;
  opens com.cosium.vet.utils;
  opens com.cosium.vet.command;
  opens com.cosium.vet.command.autocomplete;
  opens com.cosium.vet.command.checkout;
  opens com.cosium.vet.command.checkout_new;
  opens com.cosium.vet.command.fire_and_forget;
  opens com.cosium.vet.command.new_;
  opens com.cosium.vet.command.pull;
  opens com.cosium.vet.command.push;
  opens com.cosium.vet.command.status;
  opens com.cosium.vet.command.track;
  opens com.cosium.vet.command.untrack;

  exports com.cosium.vet.thirdparty.apache_commons_io;
  exports com.cosium.vet.thirdparty.apache_commons_lang3;
}
