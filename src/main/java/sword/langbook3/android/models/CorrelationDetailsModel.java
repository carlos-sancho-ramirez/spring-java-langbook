package sword.langbook3.android.models;

import sword.collections.ImmutableMap;
import sword.collections.ImmutableSet;
import sword.collections.Predicate;
import sword.collections.SortUtils;
import sword.langbook3.android.db.ImmutableCorrelation;

public final class CorrelationDetailsModel<AlphabetId, CorrelationId, AcceptationId> implements CorrelationDetails<AlphabetId, CorrelationId, AcceptationId> {

    private final ImmutableMap<AlphabetId, String> _alphabets;
    private final ImmutableCorrelation<AlphabetId> _correlation;
    private final ImmutableMap<AcceptationId, String> _acceptations;
    private final ImmutableMap<AlphabetId, ImmutableSet<CorrelationId>> _relatedCorrelationsByAlphabet;
    private final ImmutableMap<CorrelationId, ImmutableCorrelation<AlphabetId>> _relatedCorrelations;

    public CorrelationDetailsModel(
            ImmutableMap<AlphabetId, String> alphabets,
            ImmutableCorrelation<AlphabetId> correlation,
            ImmutableMap<AcceptationId, String> acceptations,
            ImmutableMap<AlphabetId, ImmutableSet<CorrelationId>> relatedCorrelationsByAlphabet,
            ImmutableMap<CorrelationId, ImmutableCorrelation<AlphabetId>> relatedCorrelations) {
        if (alphabets == null || correlation == null || acceptations == null ||
                relatedCorrelationsByAlphabet == null || relatedCorrelations == null) {
            throw new IllegalArgumentException();
        }

        final Predicate<String> isNull = SortUtils::isNull;
        if (correlation.isEmpty() || correlation.anyMatch(isNull)) {
            throw new IllegalArgumentException();
        }

        final ImmutableSet<AlphabetId> alphabetsKeySet = alphabets.keySet();
        final ImmutableSet<AlphabetId> correlationKeySet = correlation.keySet();
        final ImmutableSet<CorrelationId> relatedCorrelationsKeySet = relatedCorrelations.keySet();
        if (correlationKeySet.anyMatch(alphabet -> !alphabetsKeySet.contains(alphabet)) ||
                alphabets.anyMatch(isNull)) {
            throw new IllegalArgumentException();
        }

        if (acceptations.anyMatch(isNull)) {
            throw new IllegalArgumentException();
        }

        if (!correlationKeySet.equals(relatedCorrelationsByAlphabet.keySet()) ||
                relatedCorrelationsByAlphabet.anyMatch(SortUtils::isNull)) {
            throw new IllegalArgumentException();
        }

        for (ImmutableSet<CorrelationId> set : relatedCorrelationsByAlphabet) {
            if (set.anyMatch(v -> !relatedCorrelationsKeySet.contains(v))) {
                throw new IllegalArgumentException();
            }
        }

        _alphabets = alphabets;
        _correlation = correlation;
        _acceptations = acceptations;
        _relatedCorrelationsByAlphabet = relatedCorrelationsByAlphabet;
        _relatedCorrelations = relatedCorrelations;
    }

    @Override
    public ImmutableMap<AlphabetId, String> getAlphabets() {
        return _alphabets;
    }

    @Override
    public ImmutableCorrelation<AlphabetId> getCorrelation() {
        return _correlation;
    }

    @Override
    public ImmutableMap<AcceptationId, String> getAcceptations() {
        return _acceptations;
    }

    @Override
    public ImmutableMap<AlphabetId, ImmutableSet<CorrelationId>> getRelatedCorrelationsByAlphabet() {
        return _relatedCorrelationsByAlphabet;
    }

    @Override
    public ImmutableMap<CorrelationId, ImmutableCorrelation<AlphabetId>> getRelatedCorrelations() {
        return _relatedCorrelations;
    }
}
