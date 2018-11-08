<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : list-product.xsl
    Created on : November 7, 2018, 3:48 PM
    Author     : MinhTran
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    
    <xsl:param name="mainImage"/>
    <xsl:template match="/">
        <div class="slider">
            <div class="slide active">
                <img src="{$mainImage}"/>
            </div>
            <xsl:for-each select="images/image">
                <div class="slide">
                    <img src="{url}"/>
                </div>
            </xsl:for-each>
        </div>
        <button class="slider-navigator previous" onclick="moveSlider('previous', this)">Trước</button>
        <button class="slider-navigator next" onclick="moveSlider('next', this)">Sau</button>
    </xsl:template>

</xsl:stylesheet>
