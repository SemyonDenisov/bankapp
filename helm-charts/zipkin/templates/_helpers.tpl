{{/*
Return the application name
*/}}
{{- define "zipkin.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "zipkin.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- include "zipkin.name" . | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
