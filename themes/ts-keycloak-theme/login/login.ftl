<#import "template.ftl" as layout>

<@layout.registrationLayout displayMessage = !messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>

  <#if section = "header">
    ${msg("loginAccountTitle")}
  <#elseif section = "form">
    <div id="kc-form">
      <div id="kc-form-wrapper">
        <#if realm.password>
          <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">

            <#if !usernameHidden??>
              <div class="ts-radio-group-tab">
                <label>
                  <input type="radio" name="radio" value="mkysLogin" checked>
                  <span>${msg("mkysLogin")}</span>
                </label>
                <label>
                  <input type="radio" name="radio" value="ldapLogin">
                  <span>${msg("ldapLogin")}</span>
                </label>
              </div>
            </#if>
            
            <#if !usernameHidden??>
                
              <label for="username" class="${properties.kcLabelClass!}"><#if !realm.loginWithEmailAllowed>${msg("usernameOrEmail")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if></label>
              <input tabindex="1" id="username" class="${properties.kcInputClass!}" name="username" value="${(login.username!'')}"  type="text" autofocus autocomplete="off" aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>" />

              <#if messagesPerField.existsError('username','password')>
                <span id="input-error" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                  ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
                </span>
              </#if>
            </#if>

            <label for="password" class="${properties.kcLabelClass!}">${msg("password")}</label>
            <input tabindex="2" id="password" class="${properties.kcInputClass!}" name="password" type="password" autocomplete="off" aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>" />

            <#if usernameHidden?? && messagesPerField.existsError('username','password')>
              <span id="input-error" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
              </span>
            </#if>

            <#if realm.resetPasswordAllowed>
              <div class="text-right">
                <a tabindex="5" class="ts-forgot-password-link" href="${url.loginResetCredentialsUrl}">${msg("doForgotPassword")}</a>
              </div>
            </#if>

            <#if recaptchaRequired??>
              <div class="g-recaptcha"  data-sitekey="${recaptchaSiteKey}"></div>
            </#if>

            <div id="kc-form-buttons" class="${properties.kcFormGroupClass!}">
                <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                <input tabindex="4" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>
            </div>
          </form>
        </#if>
      </div>
    </div>
  <#elseif section = "info" >
    <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
      <div id="kc-registration-container">
        <div id="kc-registration">
          <span>${msg("noAccount")} <a tabindex="6" href="${url.registrationUrl}">${msg("doRegister")}</a></span>
        </div>
      </div>
    </#if>
  </#if>

</@layout.registrationLayout>