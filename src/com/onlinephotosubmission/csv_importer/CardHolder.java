package com.onlinephotosubmission.csv_importer;

import java.util.Arrays;

/**
 * Created by Brandon on 7/6/2017.
 */
class CardHolder {

    private static Integer organizationId;

    private static String[] header;
    private static int supportingDocsRequiredIndex = -1;

    private static final int EMAIL_INDEX = 0;
    private static final int ID_INDEX = 1;
    private static final String SUPPORTING_DOCS_REQD_HEADER = "SupportingDocumentsRequired";
    private String email;
    private String id;
    private String supportingDocsRequired;
    private String inputString;
    private String delimiter;
    String[] fieldValues;

    CardHolder() {

    }

    CardHolder(String delimiter, String inputString) {

        this.inputString = inputString;
        this.delimiter = delimiter;
        this.parseInputString();
    }

    public void setDelimiter(String delimiter) {

        this.delimiter = delimiter;
    }

    String getEmail() {

        return email;
    }

    void setEmail(String inputEmail) {

        email = inputEmail;
    }

    public String getId() {

        return id;
    }

    void setId(String inputID) {

        id = inputID;
    }

    public static int getOrganizationId() {

        return organizationId;
    }

    public static void setOrganizationId(int organizationId) throws IllegalAccessException {

        if (CardHolder.organizationId != null) {
            throw new IllegalAccessException("Organization id can only be set once and never modified.");
        }

        CardHolder.organizationId = organizationId;
    }

    public static String[] getHeader() {

        return header;
    }

    public static void setHeader(String[] header) {

        Arrays.parallelSetAll(header, (i) -> header[ i ].trim());
        supportingDocsRequiredIndex = Arrays.asList(header).indexOf(SUPPORTING_DOCS_REQD_HEADER);
        CardHolder.header = header;
    }

    public static String csvHeader() {

        return String.join(", ", header);
    }

    public void parseInputString() {

        fieldValues = inputString.split(delimiter);
        Arrays.parallelSetAll(fieldValues, (i) -> fieldValues[ i ].trim());

        email = fieldValues[ EMAIL_INDEX ];
        id = fieldValues[ ID_INDEX ];
        if (supportingDocsRequiredIndex >= 0) supportingDocsRequired = fieldValues[ supportingDocsRequiredIndex ];
    }

    public String toJSON() {

        return toJSON(false);
    }

    public String toJSON(boolean forUpdate) {

        StringBuilder json = new StringBuilder("{ \"email\":\"" + email + "\",");
        json.append(forUpdate ? "" : "\"customFields\":");
        json.append(getCustomFieldsAsJSON(forUpdate) + ", ");
        json.append("\"identifier\":\"" + id + "\"" + getSupportingDocsRequiredJSON() + " }");
        System.out.println(json.toString());
        return json.toString();
    }

    private String getSupportingDocsRequiredJSON() {

        if (supportingDocsRequiredIndex < 0) return "";
        else return ", \"additionalPhotoRequired\":" + supportingDocsRequired;
    }

    private String getCustomFieldsAsJSON(boolean forUpdate) {

        StringBuilder customFieldsAsJSON = new StringBuilder(forUpdate ? "" : "{");
        for (int i = 2; i < header.length; i++) {
            if (i == supportingDocsRequiredIndex) continue;
            customFieldsAsJSON.append("\"" + header[ i ] + "\":\"" + fieldValues[ i ].replaceAll("\"", "") + "\",");
        }
        customFieldsAsJSON.deleteCharAt(customFieldsAsJSON.length() - 1);
        customFieldsAsJSON.append(forUpdate ? "" : "}");
        return customFieldsAsJSON.toString();
    }

    @Override
    public String toString() {

        return String.join(", ", fieldValues);
    }

    public boolean validate() {

        return (email.isEmpty() || id.isEmpty()) ? false : true;
    }
}
