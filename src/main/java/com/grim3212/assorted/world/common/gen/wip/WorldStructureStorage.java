package com.grim3212.assorted.world.common.gen.wip;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;

public class WorldStructureStorage extends WorldSavedData {

	private final List<WorldStructureData> structures = Lists.newArrayList();

	public WorldStructureStorage() {
		super(IDENTIFIER);
	}

	@Nullable
	public WorldStructureData getStructureAt(BlockPos pos) {
		for (WorldStructureData data : structures) {
			for (MutableBoundingBox check : data.getStructures()) {
				if (check.isVecInside(pos)) {
					return data;
				}
			}
		}

		return null;
	}

	public boolean isStructureAt(BlockPos pos) {
		return isStructureAt(pos, new String[0]);
	}

	public boolean isStructureAt(BlockPos pos, String... structs) {
		for (int i = 0; i < structures.size(); i++) {
			if (structs.length > 0) {
				for (int j = 0; j < structs.length; j++) {
					if (structs[j].equals(structures.get(i).getStructName())) {
						for (MutableBoundingBox check : structures.get(i).getStructures()) {
							if (check.isVecInside(pos)) {
								return true;
							}
						}
					}
				}
			} else {
				for (MutableBoundingBox data : structures.get(i).getStructures()) {
					if (data.isVecInside(pos)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Check if the box intersects with a structure of any type
	 * 
	 * @param box
	 * @return
	 */
	public boolean intersects(MutableBoundingBox box) {
		return intersects(box, new String[0]);
	}

	/**
	 * Check if a MutableBoundingBox intersects any structures
	 * 
	 * if given a string array then it will consult only those structs otherwise it
	 * will check all structures
	 * 
	 * @param box
	 * @param structs
	 * @return
	 */
	public boolean intersects(MutableBoundingBox box, String... structs) {
		for (int i = 0; i < structures.size(); i++) {
			if (structs.length > 0) {
				for (int j = 0; j < structs.length; j++) {
					if (structs[j].equals(structures.get(i).getStructName())) {
						for (MutableBoundingBox check : structures.get(i).getStructures()) {
							if (box.intersectsWith(check)) {
								return true;
							}
						}
					}
				}
			} else {
				for (MutableBoundingBox check : structures.get(i).getStructures()) {
					if (box.intersectsWith(check)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public WorldStructureData getStructureData(String structName) {
		for (int i = 0; i < structures.size(); i++) {
			if (structName.equals(structures.get(i).getStructName()))
				return structures.get(i);
		}

		// If no account found, make one and run method again
		setStructureData(new WorldStructureData(structName));
		return getStructureData(structName);
	}

	public void setStructureData(WorldStructureData account) {
		boolean hasData = false;
		for (int i = 0; i < structures.size(); i++) {
			if (account.getStructName().equals(structures.get(i).getStructName())) {
				structures.set(i, account);
				hasData = true;
			}
		}

		if (!hasData)
			structures.add(account);
		markDirty();
	}

	@Override
	public void read(@Nonnull CompoundNBT nbt) {
		if (nbt.contains("Structures")) {
			// Clear structure bbs
			structures.clear();

			ListNBT structList = nbt.getList("Structures", NBT.TAG_COMPOUND);

			for (int i = 0; i < structList.size(); i++) {
				CompoundNBT struct = structList.getCompound(i);

				if (struct.contains("StructName") && struct.contains("BBs")) {
					WorldStructureData data = new WorldStructureData(struct.getString("StructName"));

					ListNBT bbs = struct.getList("BBs", NBT.TAG_INT_ARRAY);
					for (int j = 0; j < bbs.size(); j++) {
						data.getStructures().add(new MutableBoundingBox(bbs.getIntArray(i)));
					}

					// Add the structure data back
					structures.add(data);
				}
			}
		}
	}

	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT nbt) {
		ListNBT structList = new ListNBT();

		for (WorldStructureData data : structures) {
			CompoundNBT struct = new CompoundNBT();

			struct.putString("StructName", data.getStructName());

			ListNBT tagList = new ListNBT();
			for (MutableBoundingBox sbb : data.getStructures()) {
				tagList.add(sbb.toNBTTagIntArray());
			}
			struct.put("BBs", tagList);

			structList.add(struct);
		}

		nbt.put("Structures", structList);

		return nbt;
	}

	public void addBBSave(String structName, MutableBoundingBox bb) {
		getStructureData(structName).getStructures().add(bb);
		markDirty();
	}

	public static final String IDENTIFIER = "AssortedWorld-Structures";

	public static WorldStructureStorage getStructureStorage(ServerWorld world) {
		WorldStructureStorage data = (WorldStructureStorage) world.getSavedData().getOrCreate(WorldStructureStorage::new, IDENTIFIER);
		if (data == null) {
			data = new WorldStructureStorage();
			world.getSavedData().set(data);
		}
		return data;
	}

}