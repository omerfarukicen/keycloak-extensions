package tr.com.trs.extension;

import org.junit.jupiter.api.Test;

class MailTest {
    @Test
    void testMailIcerik(){
        String message = """
                    Hoşgeldiniz ,
                    Kullanıcınız kimlik sistemine başarılı şekilde kayıt edilmiştir.
                    Kullanıcı adınız: %s
                    Giriş sayfasında bulunan "Şifremi Unuttum" bölümünden şifrenizi oluşturabilirsiniz.
                    Giriş sayfası: %s
                    """.formatted("omer", "Adres");

        System.out.println(message);
    }


}
