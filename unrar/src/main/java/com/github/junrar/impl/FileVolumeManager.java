/*
 * This file is part of seedbox <github.com/seedbox>.
 *
 * seedbox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * seedbox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with seedbox.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.junrar.impl;

import java.io.File;
import java.io.IOException;

import com.github.junrar.Archive;
import com.github.junrar.IArchive;
import com.github.junrar.Volume;
import com.github.junrar.VolumeManager;
import com.github.junrar.util.VolumeHelper;


/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class FileVolumeManager implements VolumeManager {
	private final File firstVolume;

	public FileVolumeManager(File firstVolume) {
		this.firstVolume = firstVolume;
	}

	@Override
	public Volume nextArchive(IArchive archive, Volume last)
			throws IOException {
		if (last == null)
			return new FileVolume(archive, firstVolume);

		FileVolume lastFileVolume = (FileVolume) last;
		boolean oldNumbering = !archive.getMainHeader().isNewNumbering()
				|| archive.isOldFormat();
		String nextName = VolumeHelper.nextVolumeName(lastFileVolume.getFile()
				.getAbsolutePath(), oldNumbering);
		File nextVolume = new File(nextName);

		return new FileVolume(archive, nextVolume);
	}
}
