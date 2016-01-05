/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package me.kenzierocks.autoergel.osadata.util.persistence;

import java.util.Optional;

import me.kenzierocks.autoergel.osadata.data.DataContainer;
import me.kenzierocks.autoergel.osadata.data.DataSerializable;
import me.kenzierocks.autoergel.osadata.data.DataView;
import me.kenzierocks.autoergel.osadata.util.ResettableBuilder;

/**
 * Represents a builder that can take a {@link DataContainer} and create a new
 * instance of a {@link DataSerializable}. The builder should be a singleton and
 * may not exist for every data serializable.
 *
 * @param <T>
 *            The type of data serializable this builder can build
 */
public interface DataBuilder<T extends DataSerializable>
        extends ResettableBuilder<T, DataBuilder<T>> {

    /**
     * Attempts to build the provided {@link DataSerializable} from the given
     * {@link DataView}. If the {@link DataView} is invalid or missing necessary
     * information to complete building the {@link DataSerializable},
     * {@link Optional#empty()} may be returned.
     *
     * @param container
     *            The container containing all necessary data
     * @return The instance of the {@link DataSerializable}, if successful
     * @throws InvalidDataException
     *             In the event that the builder is unable to properly construct
     *             the data serializable from the data view
     */
    Optional<T> build(DataView container) throws InvalidDataException;

    @Override
    default DataBuilder<T> reset() {
        return this;
    }

    @Override
    default DataBuilder<T> from(T value) {
        return this;
    }
}
