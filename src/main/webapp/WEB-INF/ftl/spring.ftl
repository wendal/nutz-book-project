<#ftl strip_whitespace=true>
<#--
 * message as m
 * Macro to translate a message code into a message.
 -->
<#macro m code>${mvcs[(code)]}</#macro>
<#--
 * messageText as mt
 *
 * Macro to translate a message code into a message,
 * using the given default text if no message found.
 -->
<#macro mt code, text>${mvcs[(code)]}</#macro>