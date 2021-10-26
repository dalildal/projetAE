package be.vinci.pae.api.utils;

import java.time.LocalDateTime;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

class MyFormatter extends SimpleFormatter {

  public String format(LogRecord record) {
    StringBuffer s = new StringBuffer(1000);
    s.append("<" + (record.getSequenceNumber() - 2) + ">\n");
    s.append(LocalDateTime.now() + "\n");
    s.append(record.getLevel() + ": " + record.getMessage() + "\n");
    s.append("\n");
    return s.toString();
  }

}
