/*
 * MIT License
 *
 * Copyright (c) 2025 fixyldev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.fixyl.componentviewer.util;

import java.util.ArrayList;
import java.util.List;

public final class Lists {

    private Lists() {}

    /**
     * Check whether a given List is mutable.
     * <p>
     * Note: {@code false} is also returned if the given list
     * doesn't permit {@code null} elements or the
     * {@code addLast} operation.
     *
     * @param <T> the type of the list
     * @param list to check for
     * @return {@code true} if mutable, {@code false} otherwise
     */
    public static <T> boolean isMutable(List<T> list) {
        try {
            list.addLast(null);
        } catch (UnsupportedOperationException | NullPointerException e) {
            return false;
        }

        list.removeLast();
        return true;
    }

    /**
     * Returns a mutable {@link ArrayList} using the copy-constructor
     * if the given list is immutable. If the given list is already
     * mutable, the same instance is returned.
     *
     * @param <T> the type of the list
     * @param list either mutable or immutable
     * @return a mutable list with the same contents
     */
    public static <T> List<T> makeMutable(List<T> list) {
        return (Lists.isMutable(list)) ? list : new ArrayList<>(list);
    }
}
