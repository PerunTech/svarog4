/*******************************************************************************
 *   Copyright (c) 2013, 2019 Perun Technologii DOOEL Skopje.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Apache License
 *   Version 2.0 or the Svarog License Agreement (the "License");
 *   You may not use this file except in compliance with the License. 
 *  
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See LICENSE file in the project root for the specific language governing 
 *   permissions and limitations under the License.
 *  
 *******************************************************************************/
package com.prtech.svarog;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.prtech.svarog_common.DbDataObject;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Lineal;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Polygonal;
import org.locationtech.jts.geom.Puntal;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedLineString;
import org.locationtech.jts.geom.prep.PreparedPoint;
import org.locationtech.jts.geom.prep.PreparedPolygon;
import org.locationtech.jts.index.strtree.STRtree;
import com.prtech.svarog_geojson.GeoJsonWriter;

/**
 * Abstract Spatial Data services are provided by this class. The SvSDITile
 * shall be inherited by different Spatial Tile types which will be used by the
 * SvGeometry to allow tiling of GIS datasets.
 * 
 * @author ristepejov
 *
 */
public abstract class SvSDITile {
	private static final Logger log4j = SvConf.getLogger(SvSDITile.class);

	protected STRtree tileIndex = null;
	// GeometryCollection tileGeometries= null;

	public enum SDIRelation {
		INTERSECTS, COVEREDBY, CONTAINS, COVERS, CROSSES, DISJOINT, EQUALS, OVERLAPS, WITHIN, TOUCHES
	};

	protected Long tileTypeId = null;
	protected String tilelId = null;
	protected PreparedGeometry tileGeometry = null;
	protected Boolean isTileDirty = true;

	protected Collection<Geometry> internalGeometries = new ArrayList<Geometry>();
	protected Collection<Geometry> borderGeometries = new ArrayList<Geometry>();

	void prepareEnvelope(Object envGeom) throws SvException
	{
		Geometry tempGeom = null;
		if(envGeom instanceof Envelope)
			tempGeom = SvUtil.sdiFactory.toGeometry((Envelope)envGeom);
		else if (envGeom instanceof Geometry)
			tempGeom = (Geometry)envGeom;
		else throw (new SvException("sys.error.bad_tile_geometry", svCONST.systemUser));
		
		if (!(tempGeom instanceof Polygon))
			throw (new SvException("sys.error.bad_tile_geometry", svCONST.systemUser));
		
		tileGeometry = new PreparedPolygon((Polygonal) tempGeom); 

	}
	PreparedGeometry getPreparedGeom(Geometry g) {
		PreparedGeometry pg = null;
		if (g instanceof Polygonal)
			pg = new PreparedPolygon((Polygonal) g);
		else if (g instanceof Lineal)
			pg = new PreparedLineString((Lineal) g);
		else if (g instanceof Puntal)
			pg = new PreparedPoint((Puntal) g);

		return pg;
	}

	public Envelope getEnvelope() {
		return this.tileGeometry.getGeometry().getEnvelopeInternal();
	}

	abstract GeometryCollection loadGeometries() throws SvException;

	public void loadTile() throws SvException {
		if (!isTileDirty)
			return;

		ReentrantLock lock = null;
		try {
			lock = SvLock.getLock(getTileId(), true, SvConf.getMaxLockTimeout());
			if (lock != null) {
				tileIndex = new STRtree();
				internalGeometries.clear();
				borderGeometries.clear();
				Point centroid = null;
				GeometryCollection gcl = loadGeometries();
				for (int i = 0; i < gcl.getNumGeometries(); i++) {
					Geometry geom = gcl.getGeometryN(i);
					if(geom==null)
						continue;
					tileIndex.insert(geom.getEnvelopeInternal(), getPreparedGeom(geom));
					if (geom.getUserData() != null && geom.getUserData() instanceof DbDataObject) {
						centroid = SvGeometry.getCentroid((DbDataObject) geom.getUserData());
					}
					if (centroid == null)
						centroid = SvGeometry.calculateCentroid(geom);

					if (tileGeometry.covers(centroid))
						internalGeometries.add(geom);
					else
						borderGeometries.add(geom);
				}

				tileIndex.build();
				isTileDirty = false;

			} else
				log4j.warn("Failed to acquire lock" + getTileId());

		} finally {
			if (lock != null)
				SvLock.releaseLock(getTileId(), lock);
		}

	}

	public Set<Geometry> getRelations(Geometry geom, SDIRelation relation) throws SvException {
		return getRelations(geom, relation, false);
	}

	public Set<Geometry> getRelations(Geometry geom, SDIRelation relation, Boolean returnOnlyInternal)
			throws SvException {
		return (Set<Geometry>) getRelations(geom, relation, returnOnlyInternal, false);
	}

	public List<Geometry> getRelationsList(Geometry geom, SDIRelation relation, Boolean returnOnlyInternal)
			throws SvException {
		return (List<Geometry>) getRelations(geom, relation, returnOnlyInternal, true);
	}

	public Collection<Geometry> getRelations(Geometry geom, SDIRelation relation, Boolean returnOnlyInternal,
			Boolean returnDuplicates) throws SvException {
		loadTile();
		Collection<Geometry> l = returnDuplicates ? new ArrayList<>() : new HashSet<>();
		@SuppressWarnings("unchecked")
		List<PreparedGeometry> geoms = tileIndex.query(geom.getEnvelopeInternal());
		boolean relates = false;
		for (PreparedGeometry g : geoms) {
			if (returnOnlyInternal && internalGeometries.contains(g.getGeometry()))
				continue;

			switch (relation) {
			case INTERSECTS:
				relates = g.intersects(geom);
				break;
			case COVEREDBY:
				relates = g.coveredBy(geom);
				break;
			case CONTAINS:
				relates = g.contains(geom);
				break;
			case CROSSES:
				relates = g.crosses(geom);
				break;
			case COVERS:
				relates = g.covers(geom);
				break;
			case DISJOINT:
				relates = g.disjoint(geom);
				break;
			case EQUALS:
				relates = g.equals(geom);
				break;
			case OVERLAPS:
				relates = g.overlaps(geom);
				break;
			case WITHIN:
				relates = g.within(geom);
				break;
			case TOUCHES:
				relates = g.touches(geom);
				break;
			default:
				relates = false;
			}
			if (relates)
				l.add(g.getGeometry());
		}
		return l;
	}

	public Collection<Geometry> getBorderGeometries() throws SvException {
		loadTile();
		return borderGeometries;
	}

	public Collection<Geometry> getInternalGeometries() throws SvException {
		loadTile();
		return internalGeometries;
	}

	protected String getTileId() {
		return tileTypeId.toString() + "." + tilelId;
	}

	public void getGeoJSONTile(Writer writer) throws IOException, SvException {
		GeoJsonWriter jtsWriter = new GeoJsonWriter();
		jtsWriter.write(getInternalGeomCollection(), writer);
	}

	public String getGeoJSONTile(boolean useFeatureType) throws SvException {
		String geoJson = "";
		GeoJsonWriter jtsWriter = new GeoJsonWriter();
		jtsWriter.setUseFeatureType(useFeatureType);
		geoJson = jtsWriter.write(getInternalGeomCollection());
		return geoJson;

	}

	public GeometryCollection getInternalGeomCollection() throws SvException {
		Collection<Geometry> geoms = this.getInternalGeometries();
		Geometry[] gArray = (Geometry[]) geoms.toArray(new Geometry[geoms.size()]);
		GeometryCollection retGeometries = new GeometryCollection(gArray, SvUtil.sdiFactory);
		return retGeometries;
	}

	public GeometryCollection getBorderGeomCollection() throws SvException {

		GeometryCollection retGeometries = new GeometryCollection(
				this.getBorderGeometries().toArray(new Geometry[this.getBorderGeometries().size()]), SvUtil.sdiFactory);
		return retGeometries;
	}

	public String getGeoJson() throws SvException {
		return getGeoJSONTile(true);
	}

	public String getGeoJson(boolean useFeatureType) throws SvException {
		return getGeoJSONTile(useFeatureType);
	}

	public Boolean getIsTileDirty() {
		return isTileDirty;
	}

	public void setIsTileDirty(Boolean isTileDirty) {
		this.isTileDirty = isTileDirty;
	}

	public Long getTileTypeId() {
		return tileTypeId;
	}

	public void setTileTypeId(Long tileTypeId) {
		this.tileTypeId = tileTypeId;
	}

	public String getTilelId() {
		return tilelId;
	}

	public void setTilelId(String tilelId) {
		this.tilelId = tilelId;
	}

	public void getTilelId(int[] cell) {
		if (cell != null && cell.length > 2) {
			String[] tileColRow = tilelId.split(":");
			cell[0] = Integer.parseInt(tileColRow[0]);
			cell[1] = Integer.parseInt(tileColRow[1]);
		}
	}

	public void setTilelId(int[] cell) {
		if (cell != null && cell.length > 2) {
			this.tilelId = Integer.toString(cell[0]) + ":" + Integer.toString(cell[1]);
		}
	}

}
