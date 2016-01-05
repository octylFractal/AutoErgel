/*
 * This file is part of Autoergel, licensed under the MIT License (MIT).
 *
 * Copyright (c) kenzierocks (Kenzie Togami) <http://kenzierocks.me>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.kenzierocks.autoergel.recipe;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import me.kenzierocks.autoergel.osadata.util.Tuple;
import me.kenzierocks.autoergel.recipe.AutoErgel.ItemStack;

public class CraftingData {

    private static ItemStack[][] deepCopyGrid(ItemStack[][] asLayout) {
        int len = Stream.of(asLayout).filter(Objects::nonNull)
                .mapToInt(x -> x.length).findFirst().orElse(0);
        return Stream.of(asLayout)
                .map(z -> z == null ? new ItemStack[len] : z).map(
                        x -> Stream.of(x)
                                .map(z -> z == null ? ItemStack.getNoneStack()
                                        : z)
                                .map(ItemStack::copy).toArray(ItemStack[]::new))
                .toArray(ItemStack[][]::new);
    }

    private final ItemStack[][] asLayout;
    private final List<ItemStack> asList;
    private transient ItemStack[][] cachedCopy;

    public CraftingData(ItemStack[][] asLayout, List<ItemStack> asList) {
        this.asLayout = deepCopyGrid(asLayout);
        this.asList = ImmutableList
                .copyOf(asList.stream().map(ItemStack::copy).iterator());
    }

    public ItemStack[][] getAsLayout() {
        // default to caching because it's easy.
        return getAsLayout(true);
    }

    public ItemStack[][] getAsLayout(boolean cachedCopy) {
        if (cachedCopy) {
            if (this.cachedCopy == null) {
                this.cachedCopy = getAsLayout(false);
            }
            return this.cachedCopy;
        } else {
            return deepCopyGrid(this.asLayout);
        }
    }

    public List<ItemStack> getAsList() {
        return this.asList;
    }

    public Tuple<List<ItemStack>, CraftingData>
            removeStacks(ItemStack... stacks) {
        ItemStack[][] layout = getAsLayout();
        List<ItemStack> result = ImmutableList.copyOf(Stream.of(stacks)
                .filter(Objects::nonNull).map(ItemStack::copy).map(x -> {
                    for (int r = 0; r < layout.length; r++) {
                        for (int c = 0; c < layout[0].length; c++) {
                            ItemStack atPos = layout[r][c];
                            while (atPos != null && atPos.equalIgnoringSize(x)
                                    && x.getQuantity() > 0) {
                                x.setQuantity(x.getQuantity() - 1);
                                atPos.setQuantity(atPos.getQuantity() - 1);
                                if (atPos.getQuantity() == 0) {
                                    atPos = null;
                                }
                            }
                            layout[r][c] = atPos;
                        }
                    }
                    return x.getQuantity() <= 0 ? null : x;
                }).filter(Objects::nonNull).iterator());
        return Tuple.of(result, this.withLayout(layout));
    }

    public CraftingData withLayout(ItemStack[][] layout) {
        List<ItemStack> list = Stream.of(layout).flatMap(Stream::of)
                .filter(Objects::nonNull).collect(Collectors.toList());
        return new CraftingData(layout, list);
    }

    public CraftingData withList(List<ItemStack> list) {
        ItemStack[][] layout = getAsLayout();
        Iterator<ItemStack> iter = list.iterator();
        for (int r = 0; r < layout.length && iter.hasNext(); r++) {
            for (int c = 0; c < layout[0].length && iter.hasNext(); c++) {
                layout[r][c] = iter.next();
            }
        }
        return new CraftingData(layout, list);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(this.asLayout);
        result = prime * result
                + ((this.asList == null) ? 0 : this.asList.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CraftingData)) {
            return false;
        }
        CraftingData other = (CraftingData) obj;
        if (!Arrays.deepEquals(this.asLayout, other.asLayout)) {
            return false;
        }
        if (this.asList == null) {
            if (other.asList != null) {
                return false;
            }
        } else if (!this.asList.equals(other.asList)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CraftingData[asLayout=" + Arrays.deepToString(this.asLayout)
                + ",asList=" + this.asList + "]";
    }

}
