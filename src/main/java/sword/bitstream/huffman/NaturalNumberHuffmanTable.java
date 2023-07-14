package sword.bitstream.huffman;

/**
 * Huffman table that allow encoding natural numbers.
 * Negative numbers are not part of the this table.
 * <p>
 * This table assign less bits to the smaller values and more bits to bigger ones.
 * Thus, zero is always the most probable one and then the one that takes less bits.
 * <p>
 * This Huffman table assign always amount of bits that are multiple of the given
 * bit align. Trying to fit inside the lower values and adding more bits for bigger values.
 * <p>
 * E.g. if bitAlign is 4 the resulting table will assign symbols from 0 to 7 to
 * the unique symbols with 4 bits once included, leaving the first bit as a switch
 * to extend the number of bits.
 * <code>
 * <br>&nbsp;&nbsp;0000 &rArr; 0
 * <br>&nbsp;&nbsp;0001 &rArr; 1
 * <br>&nbsp;&nbsp;0010 &rArr; 2
 * <br>&nbsp;&nbsp;0011 &rArr; 3
 * <br>&nbsp;&nbsp;0100 &rArr; 4
 * <br>&nbsp;&nbsp;0101 &rArr; 5
 * <br>&nbsp;&nbsp;0110 &rArr; 6
 * <br>&nbsp;&nbsp;0111 &rArr; 7
 * <br></code>
 * <p>
 * Note that all encoded symbols start with <code>0</code>. In reality the amount of <code>1</code> before
 * this zero reflects the number of bits for this symbol. When the zero is the first
 * one, the amount of bit for the symbol is understood to match the bit align value.
 * When there are one <code>1</code> in front the zero (<code>10</code>) then it will be the bit align
 * value multiplied by 2. Thus <code>110</code> will be <code>bitAlign * 3</code>, <code>1110</code> will be
 * <code>bitAlign * 4</code> and so on.
 * <code>
 * <br>&nbsp;&nbsp;10000000 &rArr; 8
 * <br>&nbsp;&nbsp;10000001 &rArr; 9
 * <br>&nbsp;&nbsp;...
 * <br>&nbsp;&nbsp;10111111 &rArr; 71
 * <br>&nbsp;&nbsp;110000000000 &rArr; 72
 * <br>&nbsp;&nbsp;110000000001 &rArr; 73
 * <br>&nbsp;&nbsp;...
 * <br></code>
 * <p>
 * This table can theoretically include any number, even if it is really big.
 * Technically it is currently limited to the int bounds (32-bit integer).
 * As it can include any number and numbers are infinite, this table is
 * infinite as well and its iterable will not converge.
 */
public final class NaturalNumberHuffmanTable extends AbstractNaturalNumberHuffmanTable<Integer> {

    /**
     * Create a new instance with the given bit alignment.
     * @param bitAlign Number of bits that the most probable symbols will have.
     *                 Check {@link NaturalNumberHuffmanTable} for more information.
     */
    public NaturalNumberHuffmanTable(int bitAlign) {
        super(bitAlign);
    }

    @Override
    Integer box(long value) {
        if (value > Integer.MAX_VALUE) {
            throw new AssertionError("Symbol exceeds the signed 32-bits bounds. Consider using LongNaturalNumberHuffmanTable instead.");
        }

        return (int) value;
    }
}
