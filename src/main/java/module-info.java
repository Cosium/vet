/** Created on 25/02/18. */
module com.cosium.vet {
  opens com.cosium.vet;
  opens com.cosium.vet.gerrit;
  opens com.cosium.vet.gerrit.config;
  opens com.cosium.vet.log;
  opens com.cosium.vet.git;
  opens com.cosium.vet.push;
  opens com.cosium.vet.runtime;
  opens com.cosium.vet.utils;
  opens com.cosium.vet.command;

  exports com.cosium.vet.thirdparty.apache_commons_io;
  exports com.cosium.vet.thirdparty.apache_commons_lang3;
}
