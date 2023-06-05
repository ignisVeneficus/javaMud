<?xml version='1.0' encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:g="http://graphml.graphdrawing.org/xmlns"
	xmlns:y="http://www.yworks.com/xml/graphml"
		version="1.0">
	
	<xsl:output method="xml"
			version="1.0"
			encoding="UTF-8"
			indent="yes"/>
	
	<xsl:strip-space elements="*"/>
	<xsl:variable name="levelID" select="/g:graphml/g:key[@attr.name='Szint']/@id"></xsl:variable>
	<xsl:variable name="nameID" select="/g:graphml/g:key[@attr.name='Name']/@id"></xsl:variable>
	<xsl:variable name="lastID" select="/g:graphml/g:key[@attr.name='LastNr']/@id"></xsl:variable>
	<xsl:variable name="lastNr">
		<xsl:choose>
			<xsl:when test="//g:graph/g:data[@key=$lastID]"><xsl:value-of select="//g:graph/g:data[@key=$lastID]"/></xsl:when>
			<xsl:otherwise><xsl:text>0</xsl:text></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	
	<xsl:template match="g:graph">
		<xsl:copy>
			<xsl:copy-of select="attribute::*"/>
			<xsl:apply-templates/>
			<g:data	key="{$lastID}" xml:space="preserve"><xsl:value-of select="count(//g:node)"></xsl:value-of></g:data>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="g:data[@key=$lastID]">
	</xsl:template>
	
	<xsl:template match="g:node">
		<xsl:param name="label"></xsl:param>
		<xsl:copy>
			<xsl:copy-of select="attribute::*"/>
			<xsl:choose>
			<xsl:when test="not(./data[@key=$nameID])">
				<xsl:variable name="thisN"><xsl:number format="1" level="single" count="//*[local-name()='node' and not(child::g:data[@key=$nameID])]"/></xsl:variable>
				<xsl:variable name="name"><xsl:value-of select="./g:data[@key=$levelID]"/><xsl:text>_</xsl:text><xsl:value-of select="number($lastNr)+number($thisN)"/></xsl:variable>
				<g:data key="{$nameID}"><xsl:value-of select="$name"/></g:data>
				<xsl:apply-templates>
					<xsl:with-param name="label" select="$name"></xsl:with-param>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates>
					<xsl:with-param name="label" select="./data[@key=$nameID]"></xsl:with-param>
				</xsl:apply-templates>
			</xsl:otherwise>
			</xsl:choose>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="y:ShapeNode">
		<xsl:param name="label"></xsl:param>
		<xsl:copy>
			<xsl:copy-of select="attribute::*"/>
			<y:NodeLabel alignment="center" autoSizePolicy="content" fontFamily="Dialog" fontSize="5" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" height="4.0" horizontalTextPosition="center" iconTextGap="4" textColor="#000000" verticalTextPosition="bottom" visible="true" width="4.0" x="5.5" y="5.5" modelName="internal" modelPosition="c"><xsl:value-of select="$label"/></y:NodeLabel>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="y:NodeLabel">
	<!--
		<xsl:param name="label"></xsl:param>
		<xsl:copy>
			<xsl:copy-of select="attribute::*[name()!='hasText' and name()!='modelName' and name()!='modelPosition']"/>
			<xsl:attribute name="modelName">internal</xsl:attribute>
			<xsl:attribute name="modelPosition">c</xsl:attribute>
			<! - -
			<xsl:if test="not(normalize-space(child::*[1]))">
			- - >
			<xsl:if test="(child::*[0] = '') or (count(child::*)=0)">
				<xsl:value-of select="$label"/>
			</xsl:if>
			<xsl:apply-templates>
			</xsl:apply-templates>
		</xsl:copy>
		-->
	</xsl:template>
	
	<xsl:template match="y:LabelModel|y:ModelParameter"/>
	
	<xsl:template match="/|*">
		<xsl:param name="label"></xsl:param>
		<xsl:copy>
			<xsl:copy-of select="attribute::*[local-name()!='x' and local-name()!='y']"/>
			<xsl:if test="@x">
				<xsl:attribute name="x"><xsl:value-of select="round(@x * 10) div 10"/></xsl:attribute>
			</xsl:if>
			<xsl:if test="@y">
				<xsl:attribute name="y"><xsl:value-of select="round(@y * 10) div 10"/></xsl:attribute>
			</xsl:if>
			<xsl:apply-templates>
				<xsl:with-param name="label" select="$label"></xsl:with-param>
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>