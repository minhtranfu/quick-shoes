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

    <xsl:template match="/">
        <div class="list-products">
            <xsl:if test="count(products/product) = 0">
                <h2>Không có sản phẩm nào, vui lòng chọn sản phẩm khác</h2>
            </xsl:if>
            <xsl:for-each select="products/product">
                <div onclick="showProductDetail({id})" class="product" id="product-{id}"
                     data-image="{image}" data-type="{categoryId}"
                     data-source-url="{sourceUrl}"
                     data-manufactor="{manufactorId}" data-material="{materialId}">
                    <a href="#" title="{name}">
                        <img src="{image}" alt="{name}"/>
                    </a>
                    <div class="info">
                        <a href="#" class="name">
                            <xsl:value-of select="name"/>
                        </a>
                        <span class="js-price price">
                            <xsl:value-of select="price"/>
                        </span>
                        <xsl:if test="oldPrice > 0">
                            <span class="js-price old-price">
                                <xsl:value-of select="oldPrice"/>
                            </span>
                        </xsl:if>
                    </div>
                </div>
            </xsl:for-each>
        </div>
    </xsl:template>

</xsl:stylesheet>
