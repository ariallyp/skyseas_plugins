/*
 * Copyright (c) 2012-2013, Yunnan Yuan Xin technology Co., Ltd.
 * 
 * All rights reserved.
 */
package com.skyseas.openfireplugins.authintegration;

import java.io.Serializable;
import java.util.Date;

public class UserEntity implements Serializable {

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.ID_USER
     *
     * @mbggenerated
     */
    private String idUser;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.ID_ORGANIZATION
     *
     * @mbggenerated
     */
    private String idOrganization;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.ID_ORGUNIT
     *
     * @mbggenerated
     */
    private String idOrgunit;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.SIDELINEUSERID
     *
     * @mbggenerated
     */
    private String sidelineuserid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.ORGCODE
     *
     * @mbggenerated
     */
    private String orgcode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.ORGUNITCODE
     *
     * @mbggenerated
     */
    private String orgunitcode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.CODE
     *
     * @mbggenerated
     */
    private String code;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.NAME
     *
     * @mbggenerated
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.FULLNAME
     *
     * @mbggenerated
     */
    private String fullname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.SORT
     *
     * @mbggenerated
     */
    private Integer sort;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.VAILDINT
     *
     * @mbggenerated
     */
    private Integer vaildint;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.SIDELINEINT
     *
     * @mbggenerated
     */
    private Integer sidelineint;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.PASSWORD
     *
     * @mbggenerated
     */
    private String password;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.USUALCOMMENT
     *
     * @mbggenerated
     */
    private String usualcomment;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.DESCRIPTION
     *
     * @mbggenerated
     */
    private String description;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.ALIAS
     *
     * @mbggenerated
     */
    private String alias;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.TITLE
     *
     * @mbggenerated
     */
    private String title;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.PKIMAIL
     *
     * @mbggenerated
     */
    private String pkimail;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.PHONE
     *
     * @mbggenerated
     */
    private String phone;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.MAPPINGUNITFULLNAME
     *
     * @mbggenerated
     */
    private String mappingunitfullname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.PERSONCODE
     *
     * @mbggenerated
     */
    private String personcode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.LYNCID
     *
     * @mbggenerated
     */
    private String lyncid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.LOGINNAME
     *
     * @mbggenerated
     */
    private String loginname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.LOCKDATETIME
     *
     * @mbggenerated
     */
    private Date lockdatetime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.ERRORCOUNT
     *
     * @mbggenerated
     */
    private Integer errorcount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.USEREXGCODE
     *
     * @mbggenerated
     */
    private String userexgcode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.JABBERID
     *
     * @mbggenerated
     */
    private String jabberid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.PASSWORDFOR4A
     *
     * @mbggenerated
     */
    private String passwordfor4a;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.AUTHACCOUNT
     *
     * @mbggenerated
     */
    private String authaccount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.AUTHPWD
     *
     * @mbggenerated
     */
    private String authpwd;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.OUTMAILID
     *
     * @mbggenerated
     */
    private String outmailid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.STARTDATE
     *
     * @mbggenerated
     */
    private Date startdate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.MIDDLEID
     *
     * @mbggenerated
     */
    private String middleid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_MOBILEUSERBASE_USER.LAZY_FLAG
     *
     * @mbggenerated
     */
    private String lazyFlag;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table T_MOBILEUSERBASE_USER
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.ID_USER
     *
     * @return the value of T_MOBILEUSERBASE_USER.ID_USER
     *
     * @mbggenerated
     */
    public String getIdUser() {
        return idUser;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.ID_USER
     *
     * @param idUser the value for T_MOBILEUSERBASE_USER.ID_USER
     *
     * @mbggenerated
     */
    public void setIdUser(String idUser) {
        this.idUser = idUser == null ? null : idUser.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.ID_ORGANIZATION
     *
     * @return the value of T_MOBILEUSERBASE_USER.ID_ORGANIZATION
     *
     * @mbggenerated
     */
    public String getIdOrganization() {
        return idOrganization;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.ID_ORGANIZATION
     *
     * @param idOrganization the value for T_MOBILEUSERBASE_USER.ID_ORGANIZATION
     *
     * @mbggenerated
     */
    public void setIdOrganization(String idOrganization) {
        this.idOrganization = idOrganization == null ? null : idOrganization.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.ID_ORGUNIT
     *
     * @return the value of T_MOBILEUSERBASE_USER.ID_ORGUNIT
     *
     * @mbggenerated
     */
    public String getIdOrgunit() {
        return idOrgunit;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.ID_ORGUNIT
     *
     * @param idOrgunit the value for T_MOBILEUSERBASE_USER.ID_ORGUNIT
     *
     * @mbggenerated
     */
    public void setIdOrgunit(String idOrgunit) {
        this.idOrgunit = idOrgunit == null ? null : idOrgunit.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.SIDELINEUSERID
     *
     * @return the value of T_MOBILEUSERBASE_USER.SIDELINEUSERID
     *
     * @mbggenerated
     */
    public String getSidelineuserid() {
        return sidelineuserid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.SIDELINEUSERID
     *
     * @param sidelineuserid the value for T_MOBILEUSERBASE_USER.SIDELINEUSERID
     *
     * @mbggenerated
     */
    public void setSidelineuserid(String sidelineuserid) {
        this.sidelineuserid = sidelineuserid == null ? null : sidelineuserid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.ORGCODE
     *
     * @return the value of T_MOBILEUSERBASE_USER.ORGCODE
     *
     * @mbggenerated
     */
    public String getOrgcode() {
        return orgcode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.ORGCODE
     *
     * @param orgcode the value for T_MOBILEUSERBASE_USER.ORGCODE
     *
     * @mbggenerated
     */
    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode == null ? null : orgcode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.ORGUNITCODE
     *
     * @return the value of T_MOBILEUSERBASE_USER.ORGUNITCODE
     *
     * @mbggenerated
     */
    public String getOrgunitcode() {
        return orgunitcode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.ORGUNITCODE
     *
     * @param orgunitcode the value for T_MOBILEUSERBASE_USER.ORGUNITCODE
     *
     * @mbggenerated
     */
    public void setOrgunitcode(String orgunitcode) {
        this.orgunitcode = orgunitcode == null ? null : orgunitcode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.CODE
     *
     * @return the value of T_MOBILEUSERBASE_USER.CODE
     *
     * @mbggenerated
     */
    public String getCode() {
        return code;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.CODE
     *
     * @param code the value for T_MOBILEUSERBASE_USER.CODE
     *
     * @mbggenerated
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.NAME
     *
     * @return the value of T_MOBILEUSERBASE_USER.NAME
     *
     * @mbggenerated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.NAME
     *
     * @param name the value for T_MOBILEUSERBASE_USER.NAME
     *
     * @mbggenerated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.FULLNAME
     *
     * @return the value of T_MOBILEUSERBASE_USER.FULLNAME
     *
     * @mbggenerated
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.FULLNAME
     *
     * @param fullname the value for T_MOBILEUSERBASE_USER.FULLNAME
     *
     * @mbggenerated
     */
    public void setFullname(String fullname) {
        this.fullname = fullname == null ? null : fullname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.SORT
     *
     * @return the value of T_MOBILEUSERBASE_USER.SORT
     *
     * @mbggenerated
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.SORT
     *
     * @param sort the value for T_MOBILEUSERBASE_USER.SORT
     *
     * @mbggenerated
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.VAILDINT
     *
     * @return the value of T_MOBILEUSERBASE_USER.VAILDINT
     *
     * @mbggenerated
     */
    public Integer getVaildint() {
        return vaildint;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.VAILDINT
     *
     * @param vaildint the value for T_MOBILEUSERBASE_USER.VAILDINT
     *
     * @mbggenerated
     */
    public void setVaildint(Integer vaildint) {
        this.vaildint = vaildint;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.SIDELINEINT
     *
     * @return the value of T_MOBILEUSERBASE_USER.SIDELINEINT
     *
     * @mbggenerated
     */
    public Integer getSidelineint() {
        return sidelineint;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.SIDELINEINT
     *
     * @param sidelineint the value for T_MOBILEUSERBASE_USER.SIDELINEINT
     *
     * @mbggenerated
     */
    public void setSidelineint(Integer sidelineint) {
        this.sidelineint = sidelineint;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.PASSWORD
     *
     * @return the value of T_MOBILEUSERBASE_USER.PASSWORD
     *
     * @mbggenerated
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.PASSWORD
     *
     * @param password the value for T_MOBILEUSERBASE_USER.PASSWORD
     *
     * @mbggenerated
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.USUALCOMMENT
     *
     * @return the value of T_MOBILEUSERBASE_USER.USUALCOMMENT
     *
     * @mbggenerated
     */
    public String getUsualcomment() {
        return usualcomment;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.USUALCOMMENT
     *
     * @param usualcomment the value for T_MOBILEUSERBASE_USER.USUALCOMMENT
     *
     * @mbggenerated
     */
    public void setUsualcomment(String usualcomment) {
        this.usualcomment = usualcomment == null ? null : usualcomment.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.DESCRIPTION
     *
     * @return the value of T_MOBILEUSERBASE_USER.DESCRIPTION
     *
     * @mbggenerated
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.DESCRIPTION
     *
     * @param description the value for T_MOBILEUSERBASE_USER.DESCRIPTION
     *
     * @mbggenerated
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.ALIAS
     *
     * @return the value of T_MOBILEUSERBASE_USER.ALIAS
     *
     * @mbggenerated
     */
    public String getAlias() {
        return alias;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.ALIAS
     *
     * @param alias the value for T_MOBILEUSERBASE_USER.ALIAS
     *
     * @mbggenerated
     */
    public void setAlias(String alias) {
        this.alias = alias == null ? null : alias.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.TITLE
     *
     * @return the value of T_MOBILEUSERBASE_USER.TITLE
     *
     * @mbggenerated
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.TITLE
     *
     * @param title the value for T_MOBILEUSERBASE_USER.TITLE
     *
     * @mbggenerated
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.PKIMAIL
     *
     * @return the value of T_MOBILEUSERBASE_USER.PKIMAIL
     *
     * @mbggenerated
     */
    public String getPkimail() {
        return pkimail;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.PKIMAIL
     *
     * @param pkimail the value for T_MOBILEUSERBASE_USER.PKIMAIL
     *
     * @mbggenerated
     */
    public void setPkimail(String pkimail) {
        this.pkimail = pkimail == null ? null : pkimail.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.PHONE
     *
     * @return the value of T_MOBILEUSERBASE_USER.PHONE
     *
     * @mbggenerated
     */
    public String getPhone() {
        return phone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.PHONE
     *
     * @param phone the value for T_MOBILEUSERBASE_USER.PHONE
     *
     * @mbggenerated
     */
    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.MAPPINGUNITFULLNAME
     *
     * @return the value of T_MOBILEUSERBASE_USER.MAPPINGUNITFULLNAME
     *
     * @mbggenerated
     */
    public String getMappingunitfullname() {
        return mappingunitfullname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.MAPPINGUNITFULLNAME
     *
     * @param mappingunitfullname the value for T_MOBILEUSERBASE_USER.MAPPINGUNITFULLNAME
     *
     * @mbggenerated
     */
    public void setMappingunitfullname(String mappingunitfullname) {
        this.mappingunitfullname = mappingunitfullname == null ? null : mappingunitfullname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.PERSONCODE
     *
     * @return the value of T_MOBILEUSERBASE_USER.PERSONCODE
     *
     * @mbggenerated
     */
    public String getPersoncode() {
        return personcode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.PERSONCODE
     *
     * @param personcode the value for T_MOBILEUSERBASE_USER.PERSONCODE
     *
     * @mbggenerated
     */
    public void setPersoncode(String personcode) {
        this.personcode = personcode == null ? null : personcode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.LYNCID
     *
     * @return the value of T_MOBILEUSERBASE_USER.LYNCID
     *
     * @mbggenerated
     */
    public String getLyncid() {
        return lyncid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.LYNCID
     *
     * @param lyncid the value for T_MOBILEUSERBASE_USER.LYNCID
     *
     * @mbggenerated
     */
    public void setLyncid(String lyncid) {
        this.lyncid = lyncid == null ? null : lyncid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.LOGINNAME
     *
     * @return the value of T_MOBILEUSERBASE_USER.LOGINNAME
     *
     * @mbggenerated
     */
    public String getLoginname() {
        return loginname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.LOGINNAME
     *
     * @param loginname the value for T_MOBILEUSERBASE_USER.LOGINNAME
     *
     * @mbggenerated
     */
    public void setLoginname(String loginname) {
        this.loginname = loginname == null ? null : loginname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.LOCKDATETIME
     *
     * @return the value of T_MOBILEUSERBASE_USER.LOCKDATETIME
     *
     * @mbggenerated
     */
    public Date getLockdatetime() {
        return lockdatetime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.LOCKDATETIME
     *
     * @param lockdatetime the value for T_MOBILEUSERBASE_USER.LOCKDATETIME
     *
     * @mbggenerated
     */
    public void setLockdatetime(Date lockdatetime) {
        this.lockdatetime = lockdatetime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.ERRORCOUNT
     *
     * @return the value of T_MOBILEUSERBASE_USER.ERRORCOUNT
     *
     * @mbggenerated
     */
    public Integer getErrorcount() {
        return errorcount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.ERRORCOUNT
     *
     * @param errorcount the value for T_MOBILEUSERBASE_USER.ERRORCOUNT
     *
     * @mbggenerated
     */
    public void setErrorcount(Integer errorcount) {
        this.errorcount = errorcount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.USEREXGCODE
     *
     * @return the value of T_MOBILEUSERBASE_USER.USEREXGCODE
     *
     * @mbggenerated
     */
    public String getUserexgcode() {
        return userexgcode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.USEREXGCODE
     *
     * @param userexgcode the value for T_MOBILEUSERBASE_USER.USEREXGCODE
     *
     * @mbggenerated
     */
    public void setUserexgcode(String userexgcode) {
        this.userexgcode = userexgcode == null ? null : userexgcode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.JABBERID
     *
     * @return the value of T_MOBILEUSERBASE_USER.JABBERID
     *
     * @mbggenerated
     */
    public String getJabberid() {
        return jabberid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.JABBERID
     *
     * @param jabberid the value for T_MOBILEUSERBASE_USER.JABBERID
     *
     * @mbggenerated
     */
    public void setJabberid(String jabberid) {
        this.jabberid = jabberid == null ? null : jabberid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.PASSWORDFOR4A
     *
     * @return the value of T_MOBILEUSERBASE_USER.PASSWORDFOR4A
     *
     * @mbggenerated
     */
    public String getPasswordfor4a() {
        return passwordfor4a;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.PASSWORDFOR4A
     *
     * @param passwordfor4a the value for T_MOBILEUSERBASE_USER.PASSWORDFOR4A
     *
     * @mbggenerated
     */
    public void setPasswordfor4a(String passwordfor4a) {
        this.passwordfor4a = passwordfor4a == null ? null : passwordfor4a.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.AUTHACCOUNT
     *
     * @return the value of T_MOBILEUSERBASE_USER.AUTHACCOUNT
     *
     * @mbggenerated
     */
    public String getAuthaccount() {
        return authaccount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.AUTHACCOUNT
     *
     * @param authaccount the value for T_MOBILEUSERBASE_USER.AUTHACCOUNT
     *
     * @mbggenerated
     */
    public void setAuthaccount(String authaccount) {
        this.authaccount = authaccount == null ? null : authaccount.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.AUTHPWD
     *
     * @return the value of T_MOBILEUSERBASE_USER.AUTHPWD
     *
     * @mbggenerated
     */
    public String getAuthpwd() {
        return authpwd;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.AUTHPWD
     *
     * @param authpwd the value for T_MOBILEUSERBASE_USER.AUTHPWD
     *
     * @mbggenerated
     */
    public void setAuthpwd(String authpwd) {
        this.authpwd = authpwd == null ? null : authpwd.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.OUTMAILID
     *
     * @return the value of T_MOBILEUSERBASE_USER.OUTMAILID
     *
     * @mbggenerated
     */
    public String getOutmailid() {
        return outmailid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.OUTMAILID
     *
     * @param outmailid the value for T_MOBILEUSERBASE_USER.OUTMAILID
     *
     * @mbggenerated
     */
    public void setOutmailid(String outmailid) {
        this.outmailid = outmailid == null ? null : outmailid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.STARTDATE
     *
     * @return the value of T_MOBILEUSERBASE_USER.STARTDATE
     *
     * @mbggenerated
     */
    public Date getStartdate() {
        return startdate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.STARTDATE
     *
     * @param startdate the value for T_MOBILEUSERBASE_USER.STARTDATE
     *
     * @mbggenerated
     */
    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.MIDDLEID
     *
     * @return the value of T_MOBILEUSERBASE_USER.MIDDLEID
     *
     * @mbggenerated
     */
    public String getMiddleid() {
        return middleid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.MIDDLEID
     *
     * @param middleid the value for T_MOBILEUSERBASE_USER.MIDDLEID
     *
     * @mbggenerated
     */
    public void setMiddleid(String middleid) {
        this.middleid = middleid == null ? null : middleid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_MOBILEUSERBASE_USER.LAZY_FLAG
     *
     * @return the value of T_MOBILEUSERBASE_USER.LAZY_FLAG
     *
     * @mbggenerated
     */
    public String getLazyFlag() {
        return lazyFlag;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_MOBILEUSERBASE_USER.LAZY_FLAG
     *
     * @param lazyFlag the value for T_MOBILEUSERBASE_USER.LAZY_FLAG
     *
     * @mbggenerated
     */
    public void setLazyFlag(String lazyFlag) {
        this.lazyFlag = lazyFlag == null ? null : lazyFlag.trim();
    }
}