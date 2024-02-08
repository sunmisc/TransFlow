package me.sunmisc.transflow.inputs;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface Input {

    Optional<byte[]> stream() throws Exception;

    class ConcatInput implements Input {

        private static final byte[] EMPTY = new byte[0];
        private final List<Input> inputs;

        public ConcatInput(List<Input> inputs) {
            this.inputs = inputs;
        }

        @Override
        public Optional<byte[]> stream() throws Exception {
            byte[] result = EMPTY;

            for (Input input : inputs) {
                byte[] buff = input.stream().orElse(EMPTY);
                // COW
                final int len = result.length, n = buff.length;
                byte[] newElements = Arrays.copyOf(result, len + n);
                System.arraycopy(buff, 0, newElements, len, n);
                result = newElements;
            }
            return result == EMPTY
                    ? Optional.empty()
                    : Optional.of(result);
        }
    }
}
