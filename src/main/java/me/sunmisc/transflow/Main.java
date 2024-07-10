package me.sunmisc.transflow;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.sunmisc.transflow.text.ConcatText;
import me.sunmisc.transflow.text.FormattedText;
import me.sunmisc.transflow.text.ProgressBarText;
import me.sunmisc.transflow.vk.pipeline.VPipeSource;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mojang.brigadier.arguments.LongArgumentType.getLong;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static java.lang.System.in;
import static java.lang.System.out;
import static java.util.stream.Collectors.joining;

public final class Main {

    public static void main(String[] args) throws CommandSyntaxException {

        CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();

        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            dispatcher.register(literal("playlist")
                    .then(argument("token", string())
                            .then(argument("output", string())
                                    .then(argument("ownerId", longArg())
                                            .executes(context -> {
                                                String token = getString(context, "token");
                                                long ownerId = getLong(context, "ownerId");
                                                Path path = Path
                                                        .of(getString(context, "output"))
                                                        .resolve(String.valueOf(ownerId));

                                                if (Files.notExists(path)) {
                                                    try {
                                                        Files.createDirectory(path);
                                                    } catch (IOException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }

                                                return download(new VPipeSource(
                                                                httpClient,
                                                                token, ownerId),
                                                        path);
                                            })
                                    )
                            )
                    )
            );
            dispatcher.execute(
                    "playlist \"vk1.a.sfZgM-J4DUaArAcBHU-VwG8tlcRyDhE2corO2Yt2LMpKimuQXL1a6L-KPiX_UbsVQcpjj2AOu3v2wahDdczADvfbajZDLF4aI_FVArBZwhw-0JEZ2SWZsPX2Gkxk-4_NbgX5pxxHSNlTh7VORqajddZIdGmVOtVOOiR2Dtn0FWp8N4ali5nKuIC_0QdM3Ek_\" \"/media/sunmisc/shared_ntfs/bench\" 616057755", null);

            System.out.println(new ConcatText(dispatcher
                    .getSmartUsage(dispatcher.getRoot(), null)
                    .values(), "\n"));

            try (Scanner scanner = new Scanner(in)) {
                while (scanner.hasNext()) {
                    dispatcher.execute(scanner.nextLine(), null);
                }
            }
        }
    }

    private static int download(PipeSource<Audio> ps, Path to) {
        long size = ps.estimateSize();

        Fallback<Audio, AudioDownloadException>
                fallback = new DownloadFallback(size);

        new DownloadSink(fallback, ps, to).run();

        return 1;
    }
    private static class DownloadFallback
            implements Fallback<Audio, AudioDownloadException> {
        private final Set<CharSequence> skipped = ConcurrentHashMap.newKeySet();
        private final AtomicInteger progress = new AtomicInteger();
        private final long size;
        private final long currentTimeMillis;

        public DownloadFallback(long size, long currentTimeMillis) {
            this.size = size;
            this.currentTimeMillis = currentTimeMillis;
        }

        public DownloadFallback(long size) {
            this(size, System.currentTimeMillis());
        }
        @Override
        public void success(Audio result) {
            int pendingCount = progress.incrementAndGet();
            out.print(new ProgressBarText(
                    ((double) pendingCount / size) * 100));
            out.flush();

            if (pendingCount < size)
                return;

            long end = System.currentTimeMillis() - currentTimeMillis;
            LocalTime time = LocalTime.MIN
                    .plusSeconds(TimeUnit.MILLISECONDS.toSeconds(end));
            out.println(
                    new FormattedText("""
                                                    
                            elapsed time: %s
                            skipped: %s
                            %s
                            """,
                            DateTimeFormatter.ISO_TIME.format(time),
                            skipped.size(),
                            new ConcatText(skipped, "\n")
                    )
            );
        }

        @Override
        public void exception(AudioDownloadException e) {
            Audio p = e.audio();
            success(p);// ignored
            skipped.add(
                    new FormattedText(
                            "%s | exception: %s",
                            new ConcatText("",
                                    p.name(), p.authors()
                                    .map(Author::name)
                                    .collect(joining(", "))
                            ),
                            e.getMessage()
                    )
            );
        }
    }
}
