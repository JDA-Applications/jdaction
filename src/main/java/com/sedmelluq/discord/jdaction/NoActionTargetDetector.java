package com.sedmelluq.discord.jdaction;

import java.util.regex.Pattern;

public class NoActionTargetDetector {
  public static final Pattern SUBCLASS_PATTERN = Pattern.compile(
      "^\\)Lnet/dv8tion/jda/[a-z0-9/]+/restaction/([a-z0-9/]+/)?[a-zA-Z0-9]+Action;$"
  );

  public static boolean isRestActionDescriptor(String descriptor) {
    if (descriptor.endsWith(")Lnet/dv8tion/jda/api/requests/RestAction;")) {
      return true;
    }

    String suffix = descriptor.substring(descriptor.lastIndexOf(')'));
    return SUBCLASS_PATTERN.matcher(suffix).matches();
  }
}
