/*
 * #%L
 * SciJava Common shared library for SciJava software.
 * %%
 * Copyright (C) 2009 - 2014 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.scijava.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scijava.MenuEntry;
import org.scijava.MenuPath;
import org.scijava.command.CommandInfo;
import org.scijava.command.CommandService;
import org.scijava.event.EventHandler;
import org.scijava.event.EventService;
import org.scijava.io.event.IOEvent;
import org.scijava.menu.MenuConstants;
import org.scijava.module.ModuleInfo;
import org.scijava.module.ModuleService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.prefs.PrefService;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.scijava.util.FileUtils;

// TODO - RecentLocationService, WindowService, and LUTService
// all build menus dynamically (see createInfo()). We may be able to abstract a
// helper class out of these that can be used by them and future services.

/**
 * Default service for managing a menu of recently used locations.
 * <p>
 * Behavior: There is a limited number of locations presented (maxFilesShown),
 * regardless of the location length. When a location is opened, its path is
 * added to the top of the list. If an image has been saved as a new location,
 * its path is added to the top of the list.
 * </p>
 * 
 * @author Grant Harris
 * @author Curtis Rueden
 */
@Plugin(type = Service.class)
public final class DefaultRecentService extends AbstractService
	implements RecentService
{

	// -- Constants --

	/** Maximum pathname length shown. */
	private static final int MAX_DISPLAY_LENGTH = 40;

	private static final String RECENT_MENU_NAME = "Open Recent";

	private static final String RECENT_LOCATIONS_KEY = "recent";

	// -- Fields --

	@Parameter
	private EventService eventService;

	@Parameter
	private ModuleService moduleService;

	@Parameter
	private CommandService commandService;

	@Parameter
	private PrefService prefService;

	@Parameter
	private LocationService locationService;

	private List<Location> recentLocations;
	private Map<Location, ModuleInfo> recentModules;

	// -- RecentFileService methods --

	@Override
	public void add(final Location location) {
		final boolean present = recentModules.containsKey(location);

		// add path to recent locations list
		if (present) recentLocations.remove(location);
		recentLocations.add(location);

		// persist the updated list
		saveLocations();

		if (present) {
			// path already present; update linked module info
			final ModuleInfo info = recentModules.get(location);
			// TODO - update module weights
			info.update(eventService);
		}
		else {
			// new path; create linked module info
			final ModuleInfo info = createInfo(location);
			recentModules.put(location, info);

			// register the module with the module service
			moduleService.addModule(info);
		}
	}

	@Override
	public boolean remove(final Location location) {
		// remove path from recent locations list
		final boolean success = recentLocations.remove(location);

		// persist the updated list
		saveLocations();

		// remove linked module info
		final ModuleInfo info = recentModules.remove(location);
		if (info != null) moduleService.removeModule(info);

		return success;
	}

	@Override
	public void clear() {
		recentLocations.clear();
		prefService.clear(RECENT_LOCATIONS_KEY);

		// unregister the modules with the module service
		moduleService.removeModules(recentModules.values());

		recentModules.clear();
	}

	@Override
	public List<Location> getRecentLocations() {
		return Collections.unmodifiableList(recentLocations);
	}

	// -- Service methods --

	@Override
	public void initialize() {
		loadLocations();
		recentModules = new HashMap<Location, ModuleInfo>();
		for (final Location location : recentLocations) {
			recentModules.put(location, createInfo(location));
		}

		// register the modules with the module service
		moduleService.addModules(recentModules.values());
	}

	// -- Event handlers --

	@EventHandler
	protected void onEvent(final IOEvent event) {
		add(event.getLocation());
	}

	// -- Helper methods --

	/** Loads the list of recent locations from persistent storage. */
	private void loadLocations() {
		final List<String> locationPaths = prefService.getList(RECENT_LOCATIONS_KEY);
		recentLocations = new ArrayList<Location>(locationPaths.size());
		for (final String path : locationPaths) {
			recentLocations.add(locationService.create(path));
		}
	}

	/** Saves the list of recent locations to persistent storage. */
	private void saveLocations() {
		final ArrayList<String> locationPaths = new ArrayList<String>(recentLocations.size());
		for (final Location location : recentLocations) {
			location.getPath();
		}
		prefService.putList(locationPaths, RECENT_LOCATIONS_KEY);
	}

	/** Creates a {@link ModuleInfo} to reopen data at the given location. */
	private ModuleInfo createInfo(final Location location) {
		// CTR FIXME: Avoid circular dependency between
		// scijava-common and scijava-plugins-commands.
		final String commandClassName = "imagej.plugins.commands.io.OpenFile";
		final CommandInfo info = new CommandInfo(commandClassName);

		// hard code location to open as a preset
		// CTR FIXME: Generalize OpenFile command to OpenLocation.
		final HashMap<String, Object> presets = new HashMap<String, Object>();
		presets.put("inputFile", location.getPath());
		info.setPresets(presets);

		// set menu path
		final MenuPath menuPath = new MenuPath();
		menuPath.add(new MenuEntry(MenuConstants.FILE_LABEL));
		menuPath.add(new MenuEntry(RECENT_MENU_NAME));
		final MenuEntry leaf = new MenuEntry(shortPath(location));
		menuPath.add(leaf);
		info.setMenuPath(menuPath);

		// set menu position
		leaf.setWeight(0); // TODO - do this properly

		// use the same icon as File > Open
		final CommandInfo fileOpen = commandService.getCommand(commandClassName);
		if (fileOpen != null) {
			final String iconPath = fileOpen.getIconPath();
			info.setIconPath(iconPath);
		}

		return info;
	}

	/** Shortens the given path to ensure it conforms to a maximum length. */
	private String shortPath(final Location location) {
		// TODO - shorten path name as needed
		return FileUtils.limitPath(location.toString(), MAX_DISPLAY_LENGTH);
	}

}
