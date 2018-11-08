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
        <div class="section-title">Các phiên bản size và màu hiện có:</div>
        <div class="product-versions">
            <xsl:for-each select="/productVersions/productVersion">
                <div class="product-version">
                    <span class="size">
                        Size: <xsl:value-of select="size"/>
                    </span> - 
                    <span class="color">
                        Màu: 
                        <strong class="js-category" data-category="{colorId}"></strong>
                    </span>
                </div>
            </xsl:for-each>
        </div>
    </xsl:template>

</xsl:stylesheet>
