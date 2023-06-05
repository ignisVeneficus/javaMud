<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:g="http://graphml.graphdrawing.org/xmlns" 
		xmlns:y="http://www.yworks.com/xml/graphml" 
		xmlns:xi="http://www.w3.org/2001/XInclude" version="1.0" exclude-result-prefixes="g y xi">

	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="reqlevel"></xsl:param>
	<xsl:param name="grid">30</xsl:param>

	<xsl:strip-space elements="*"/>

	
	<xsl:variable name="levelID" select="/g:graphml/g:key[@attr.name='Szint']/@id"></xsl:variable>
	<xsl:variable name="tipusID" select="/g:graphml/g:key[@attr.name='Tipus']/@id"></xsl:variable>
	<xsl:variable name="descrID" select="/g:graphml/g:key[@attr.name='Nevesitve']/@id"></xsl:variable>
	<xsl:variable name="nameID" select="/g:graphml/g:key[@attr.name='Name']/@id"></xsl:variable>
	<xsl:variable name="shapeID" select="/g:graphml/g:key[@for='node'and @yfiles.type='nodegraphics']/@id"></xsl:variable>

	<xsl:variable name="prefixId" select="/g:graphml/g:key[@attr.name='Prefix']/@id"></xsl:variable>
	<xsl:variable name="prefix" select="/g:graphml/g:graph/g:data[@key=$prefixId]"></xsl:variable>
	
	
	<xsl:template match="/">
		<Map>
		<xsl:choose>
			<xsl:when test="$reqlevel!=''">
				<xsl:call-template name="out">
					<xsl:with-param name="list" select="//g:node[g:data[@key=$levelID]=$reqlevel]"></xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="out">
					<xsl:with-param name="list" select="//g:node"></xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		</Map>
	</xsl:template>
	<xsl:template match="text()">
	</xsl:template>
	
	<xsl:template name="out">
		<xsl:param name="list"/>
		<xsl:variable name="minx">
			<xsl:for-each select="$list/g:data[@key=$shapeID]/y:ShapeNode/y:Geometry/@x">
			<xsl:sort data-type="number"/>
			<xsl:if test="position()=1"><xsl:value-of select="number(.)-number($grid)"/></xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="maxx">
			<xsl:for-each select="$list/g:data[@key=$shapeID]/y:ShapeNode/y:Geometry/@x">
			<xsl:sort data-type="number"/>
			<xsl:if test="position()=last()"><xsl:value-of select="number(.)+number($grid)"/></xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="miny">
			<xsl:for-each select="$list/g:data[@key=$shapeID]/y:ShapeNode/y:Geometry/@y">
			<xsl:sort data-type="number"/>
			<xsl:if test="position()=1"><xsl:value-of select="number(.)-number($grid)"/></xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="maxy">
			<xsl:for-each select="$list/g:data[@key=$shapeID]/y:ShapeNode/y:Geometry/@y">
			<xsl:sort data-type="number"/>
			<xsl:if test="position()=last()"><xsl:value-of select="number(.)+number($grid)"/></xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:for-each select="$list">
			<xsl:variable name="x" select="(number(./g:data[@key=$shapeID]/y:ShapeNode/y:Geometry/@x)-number($minx) ) div number($grid)"/>
			<xsl:variable name="y" select="(number(./g:data[@key=$shapeID]/y:ShapeNode/y:Geometry/@y)-number($miny) ) div number($grid)"/>
			<xsl:choose>
				<xsl:when test="(./g:data[@key=$shapeID]/y:ShapeNode/y:Shape/@type)='ellipse'">
				</xsl:when>
				<xsl:otherwise>
					<loc x="{$x}" y="{$y}">
						<xsl:attribute name="id"><xsl:value-of select="$prefix"/><xsl:text>_</xsl:text><xsl:value-of select="./g:data[@key=$nameID]"/></xsl:attribute>
						<xsl:if test="$reqlevel=''">
							<xsl:attribute name="group"><xsl:value-of select="./g:data[@key=$levelID]"/></xsl:attribute>
						</xsl:if>
						<type>room</type>
						<xsl:if test="(./g:data[@key=$shapeID]/y:ShapeNode/y:Shape/@type)='octagon'">
						<overlay><xsl:value-of select="./g:data[@key=$descrID]"/></overlay>
						</xsl:if>
					</loc>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:variable name="id" select="./@id"/>
			<xsl:for-each select="//g:edge[@source=$id]">
				<xsl:variable name="target" select="@target"/>
				<xsl:call-template name="exit">
					<xsl:with-param name="x" select="$x"/>
					<xsl:with-param name="y" select="$y"/>
					<xsl:with-param name="minx" select="$minx"/>
					<xsl:with-param name="miny" select="$miny"/>
					<xsl:with-param name="node" select="//g:node[@id=$target]"/>
				</xsl:call-template>
			</xsl:for-each>
			<xsl:if test="$reqlevel!=''">
				<xsl:for-each select="//g:edge[@target=$id]">
					<xsl:variable name="source" select="@source"/>
					<xsl:choose>
						<xsl:when test = "$list[string(.)=string(//g:node[@id=$source])]"/>
						<xsl:otherwise>
							<xsl:call-template name="exit">
								<xsl:with-param name="x" select="$x"/>
								<xsl:with-param name="y" select="$y"/>
								<xsl:with-param name="node" select="//g:node[@id=$source]"/>
								<xsl:with-param name="minx" select="$minx"/>
								<xsl:with-param name="miny" select="$miny"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</xsl:if>
			
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="exit">
		<xsl:param name="x"/>
		<xsl:param name="y"/>
		<xsl:param name="minx"/>
		<xsl:param name="miny"/>
		<xsl:param name="node"/>
		<xsl:variable name="x2" select="(number($node/g:data[@key=$shapeID]/y:ShapeNode/y:Geometry/@x)-number($minx) ) div number($grid)"/>
		<xsl:variable name="y2" select="(number($node/g:data[@key=$shapeID]/y:ShapeNode/y:Geometry/@y)-number($miny) ) div number($grid)"/>
		<xsl:comment>to <xsl:value-of select="$node/g:data[@key=$nameID]"/></xsl:comment>
		<xsl:choose>
			<xsl:when test="(number($x)-number($x2) &lt; -1) or (number($x)-number($x2) &gt; 1) or (number($y)-number($y2) &lt; -1) or (number($y)-number($y2) &gt; 1)">
				<xsl:comment>&lt;con x1="<xsl:value-of select="$x"/>" y1="<xsl:value-of select="$y"/>" x2="<xsl:value-of select="$x2"/>" y2="<xsl:value-of select="$y2"/>"/&gt;</xsl:comment>
			</xsl:when>
			<xsl:otherwise>
				<con x1="{$x}" y1="{$y}" x2="{$x2}" y2="{$y2}"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
</xsl:stylesheet>
