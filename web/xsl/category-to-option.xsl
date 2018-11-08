<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : category-to-select.xsd.xsl
    Created on : November 8, 2018, 4:21 PM
    Author     : MinhTran
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>

    <xsl:param name="typeValue" select="type"/>
    <xsl:template match="/">
        <option value="">Tất cả</option>
        <xsl:for-each select="/categories/category[type = $typeValue]">
            <xsl:if test="name != ''">
                <option value="{id}">
                    <xsl:value-of select="name"/>
                </option>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
