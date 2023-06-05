<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:g="http://graphml.graphdrawing.org/xmlns" 
		xmlns:y="http://www.yworks.com/xml/graphml" 
		xmlns:xi="http://www.w3.org/2001/XInclude" version="1.0" exclude-result-prefixes="g y xi">

	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="NodeId"></xsl:param>
	<xsl:param name="Path"></xsl:param>
	<xsl:param name="WithMap"></xsl:param>
	<xsl:param name="grid">30</xsl:param>

	<xsl:variable name="levelID" select="/g:graphml/g:key[@attr.name='Szint']/@id"></xsl:variable>
	<xsl:variable name="tipusID" select="/g:graphml/g:key[@attr.name='Tipus']/@id"></xsl:variable>
	<xsl:variable name="descrID" select="/g:graphml/g:key[@attr.name='Nevesitve']/@id"></xsl:variable>
	<xsl:variable name="nameID" select="/g:graphml/g:key[@attr.name='Name']/@id"></xsl:variable>
	<xsl:variable name="shapeID" select="/g:graphml/g:key[@for='node'and @yfiles.type='nodegraphics']/@id"></xsl:variable>
	<xsl:variable name="prefixId" select="/g:graphml/g:key[@attr.name='Prefix']/@id"></xsl:variable>
	<xsl:variable name="prefix" select="/g:graphml/g:graph/g:data[@key=$prefixId]"></xsl:variable>
	
	
	<xsl:template match="/|*">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="text()">
	</xsl:template>
	
	
	<xsl:template match="//g:node[@id = $NodeId]">
<xsl:text>
</xsl:text>
		<Room>
			<xsl:if test="($WithMap = 'Y') or ($WithMap = 'y')">
				<xsl:attribute name="mapfile"><xsl:value-of select="$Path"/><xsl:text>/map</xsl:text></xsl:attribute>
			</xsl:if>
			<stimulus type="latas" intensity="0" shortDescr="{./g:data[@key=$nameID]}" >
				<descr>DUMMY szoveg - <xsl:value-of select="./g:data[@key=$tipusID]"/> - <xsl:value-of select="./g:data[@key=$descrID]"/></descr>
			</stimulus>
			<xsl:variable name="x" select="./g:data[@key=$shapeID]/y:ShapeNode/y:Geometry/@x"/>
			<xsl:variable name="y" select="./g:data[@key=$shapeID]/y:ShapeNode/y:Geometry/@y"/>
			<xsl:variable name="id" select="@id"/>
			
			<xsl:for-each select="//g:edge[@source=$id]">
				<xsl:variable name="target" select="@target"/>
				<xsl:call-template name="exit">
					<xsl:with-param name="x" select="$x"/>
					<xsl:with-param name="y" select="$y"/>
					<xsl:with-param name="node" select="//g:node[@id=$target]"/>
				</xsl:call-template>
			</xsl:for-each>
			<xsl:for-each select="//g:edge[@target=$id]">
				<xsl:variable name="source" select="@source"/>
				<xsl:call-template name="exit">
					<xsl:with-param name="x" select="$x"/>
					<xsl:with-param name="y" select="$y"/>
					<xsl:with-param name="node" select="//g:node[@id=$source]"/>
				</xsl:call-template>
			</xsl:for-each>
		</Room>
	</xsl:template>
	
	<xsl:template name="exit">
		<xsl:param name="x"/>
		<xsl:param name="y"/>
		<xsl:param name="node"/>
		<exit notice="50">
			<xsl:choose>
				<xsl:when test="($node/g:data[@key=$shapeID]/y:ShapeNode/y:Shape/@type)='ellipse'">
					<xsl:attribute name="direction">kifele</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="direction">
						<xsl:call-template name="direction">
							<xsl:with-param name="dx" select="number($node/g:data[@key=$shapeID]/y:ShapeNode/y:Geometry/@x)-number($x)"/>
							<xsl:with-param name="dy" select="number($node/g:data[@key=$shapeID]/y:ShapeNode/y:Geometry/@y)-number($y)"/>
						</xsl:call-template>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:attribute name="destination">
				<xsl:call-template name="file">
					<xsl:with-param name="node" select="$node"/>
				</xsl:call-template>
			</xsl:attribute>
		</exit>
	</xsl:template>
	

	
	<xsl:template name="direction">
		<xsl:param name="dx"/>
		<xsl:param name="dy"/>
		<xsl:choose>
			<xsl:when test="number($dx)=0 and number($dy)=-1*number($grid)">
				<xsl:text>észak</xsl:text>
			</xsl:when>
			<xsl:when test="number($dx)=number($grid) and number($dy)=-1*number($grid)">
				<xsl:text>északkelet</xsl:text>
			</xsl:when>
			<xsl:when test="number($dx)=number($grid) and number($dy)=0">
				<xsl:text>kelet</xsl:text>
			</xsl:when>
			<xsl:when test="number($dx)=number($grid) and number($dy)=number($grid)">
				<xsl:text>délkelet</xsl:text>
			</xsl:when>
			<xsl:when test="number($dx)=0 and number($dy)=number($grid)">
				<xsl:text>dél</xsl:text>
			</xsl:when>
			<xsl:when test="number($dx)=-1*number($grid) and number($dy)=number($grid)">
				<xsl:text>délnyugat</xsl:text>
			</xsl:when>
			<xsl:when test="number($dx)=-1*number($grid) and number($dy)=0">
				<xsl:text>nyugat</xsl:text>
			</xsl:when>
			<xsl:when test="number($dx)=-1*number($grid) and number($dy)=-1*number($grid)">
				<xsl:text>északnyugat</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>akarmerre</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="file">
		<xsl:param name="node"/>
		<xsl:value-of select="$Path"/><xsl:text>/</xsl:text><xsl:value-of select="$prefix"/><xsl:text>_</xsl:text><xsl:value-of select="$node/g:data[@key=$nameID]"/>
	</xsl:template>
	
</xsl:stylesheet>
