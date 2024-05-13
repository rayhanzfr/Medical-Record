package com.prodia.technical.authentication.security;

import com.prodia.technical.common.helper.EncryptionHelper;

public class SecurityConstant {
  /*
   * jwt config
   */
  public static String JWT_SECRET = EncryptionHelper.encrypt("secret");
  public static String OPEN_SECRET = EncryptionHelper.encrypt("open");
  public static String EXE_SECRET = EncryptionHelper.encrypt("executor");
  public static int ACCESS_TOKEN_EXP_HOUR = 1;
  public static long REFRESH_TOKEN_EXP_MINUTES = 90;
  public static String USER_NAME = EncryptionHelper.encrypt("username");
  public static String USER_AGENT = EncryptionHelper.encrypt("usa");
  public static String JWT_ID = EncryptionHelper.encrypt("jid");
  public static String USER_ACCESS_CODE = EncryptionHelper.encrypt("uac");
  public static String USER_PERMISSION = EncryptionHelper.encrypt("upr");
  public static String EXE_EMP_ID = EncryptionHelper.encrypt("eei");
  public static String EXE_NAME = EncryptionHelper.encrypt("en");
  public static String EXE_MAIL = EncryptionHelper.encrypt("em");
  public static String EXE_COMPANY_ID = EncryptionHelper.encrypt("eci");
  public static String EXE_COMPANY_NAME = EncryptionHelper.encrypt("ecn");

  /*
   * login config
   */
  public static Integer ATTEMPT_LOGIN_TIME = 1 * 360000; // in hour
  public static Long PASSWORD_VALID_DAYS = 90L;
  public static String PERMITTED_URI =
      "/admin/v3/api-docs/.*|/v3/api-docs/.*|/v3/api-docs|/admin/v3/api-docs|"
          + "/api/v1/auth/login|/auth/login|/api/v1/auth/secure/login|"
          + "/swagger-ui/.*|/users/verify/.*|/api/v1/users/verify|/api/v1/users/verify/.*|"
          + "/api/v1/users/create-password|/api/v1/users/create-password/.*|/api/v1/users/resend-verification-token|"
          + "/api/v1/users/resend-verification-token.*|/api/v1/users/send-reset-password-link|/api/v1/users/reset-password|/socket?.*|/api/v1/auth/signup|"
          + "/api/v1/obat.*";
  public static String OPEN_API =
      "/open/.*|/open/approval-html/.*|/api/v1/auth/login|/auth/login";
  public static final String MONGO_DB = "mongo";
  public static final String TABLESTORE = "tablestore";
  public static final String POSTGRES = "postgres";
}