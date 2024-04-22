package tr.com.trs.extension.constant;

public final class ExtensionsConstants {
    public static final String GIRIS_SAYFASI_PAGE = "https://staging-kimlik.turkiyesigorta.com.tr";
    public static final String GIRIS_SAYFASI_CANLI = "https://kimlik.turkiyesigorta.com.tr/";
    public static final String GIRIS_SAYFASI_DEV = "http://localhost:8080/admin/turkiye-sigorta/console";
    public static final String LDAP_USERS_DN = "ou=_ORGANIZATION,dc=trs,dc=local";
    public static final String LDAP_USER_OBJECT_CLASSES = "user";
    public static final String LDAP_UUID_LDAP_ATTRIBUTE = "objectGUID";
    public static final String LDAP_USERNAME_LDAP_ATTRIBUTE = "SamAccountName";
    public static final String LDAP_RDN_LDAP_ATTRIBUTE = "SamAccountName";
    public static final String LDAP_CONNECTION_URL = "ldap://trs.local:389/";
    public static final String LDAP_BIND_CREDENTIAL = "Y40IByl28J6W";
    public static final String LDAP_BIND_DN = "trs\\test.mkys";
    public static final String CREATE_CAPTCHA = "https://www.google.com/recaptcha/api.js?hl=";
    public static final String VALIDATE_CAPTCHA = "https://www.google.com/recaptcha/api/siteverify";
    public static final String WRONG_PASSWORD_AND_USERNAME = "Kullanıcı Adı veya Parola yanlış!";
    public static final String NO_IMAGE = "Resim doğrulanamadı!";
    public static final String USER_SON_GIRIS_TARIHI = "SONGIRISTARIHI";
    public static final String USER_TYPE = "KULLANICITIPI";
    public static final String PERSONEL_USER = "PERSONEL";

    private ExtensionsConstants() {
    }
}
