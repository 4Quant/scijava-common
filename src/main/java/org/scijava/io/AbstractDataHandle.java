
package org.scijava.io;

import java.io.IOException;

import org.scijava.plugin.AbstractWrapperPlugin;

/**
 * Abstract base class for {@link DataHandle} plugins.
 *
 * @author Curtis Rueden
 */
public abstract class AbstractDataHandle<L extends Location> extends
	AbstractWrapperPlugin<Location> implements DataHandle<L>
{

	// -- DataHandle methods --

	@Override
	public L getLocation() {
		final Location location = get();
		if (!getLocationType().isInstance(location)) {
			throw new IllegalStateException("Unsupported location type: " +
				location.getClass().getName());
		}
		@SuppressWarnings("unchecked")
		final L result = (L) location;
		return result;
	}

	@Override
	public void resetStream() throws IOException {
		seek(0);
	}

	// -- Typed methods --

	@Override
	public boolean supports(final Location location) {
		return getLocationType().isInstance(location);
	}

	@Override
	public Class<Location> getType() {
		return Location.class;
	}

}
