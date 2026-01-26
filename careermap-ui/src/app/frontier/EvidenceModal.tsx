import { useState } from 'react';

interface EvidenceModalProps {
  userId: number;
  onClose: () => void;
  onSubmit: () => void;
}

export default function EvidenceModal({ userId, onClose, onSubmit }: EvidenceModalProps) {
  const [evidenceType, setEvidenceType] = useState<'QUIZ' | 'PROJECT' | 'REPO' | 'CERT' | 'WORK_SAMPLE'>('PROJECT');
  const [rawText, setRawText] = useState('');
  const [sourceUri, setSourceUri] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!rawText.trim()) {
      setError('Please enter evidence description');
      return;
    }

    setSubmitting(true);
    setError(null);

    try {
      const response = await fetch('http://localhost:8080/api/evidence/project', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId,
          type: evidenceType,
          rawText,
          sourceUri: sourceUri || null,
        }),
      });

      const data = await response.json();

      if (!response.ok || !data.success) {
        throw new Error(data.error || 'Failed to submit evidence');
      }

      onSubmit();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4">
      <div className="bg-gray-900 border border-gray-700 rounded-lg max-w-2xl w-full p-6">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-white">Add Evidence</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-white text-2xl"
          >
            ×
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Evidence Type */}
          <div>
            <label className="block text-sm font-semibold mb-2 text-white">Evidence Type</label>
            <select
              value={evidenceType}
              onChange={(e) => setEvidenceType(e.target.value as any)}
              className="w-full px-4 py-2 bg-gray-800 border border-gray-700 rounded text-white"
            >
              <option value="PROJECT">Project</option>
              <option value="QUIZ">Quiz/Assessment</option>
              <option value="REPO">Code Repository</option>
              <option value="CERT">Certification</option>
              <option value="WORK_SAMPLE">Work Sample</option>
            </select>
          </div>

          {/* Raw Text */}
          <div>
            <label className="block text-sm font-semibold mb-2 text-white">
              Description <span className="text-red-500">*</span>
            </label>
            <textarea
              value={rawText}
              onChange={(e) => setRawText(e.target.value)}
              placeholder="Describe your project, certification, or accomplishment. Include technologies used, what you built, and any relevant details..."
              className="w-full px-4 py-2 bg-gray-800 border border-gray-700 rounded text-white min-h-32"
              required
            />
            <p className="text-xs text-gray-500 mt-1">
              AI will extract skills from this description
            </p>
          </div>

          {/* Source URI */}
          <div>
            <label className="block text-sm font-semibold mb-2 text-white">
              Source URL (optional)
            </label>
            <input
              type="url"
              value={sourceUri}
              onChange={(e) => setSourceUri(e.target.value)}
              placeholder="https://github.com/username/repo"
              className="w-full px-4 py-2 bg-gray-800 border border-gray-700 rounded text-white"
            />
          </div>

          {/* Error */}
          {error && (
            <div className="p-3 bg-red-500/20 border border-red-500 rounded text-red-300 text-sm">
              {error}
            </div>
          )}

          {/* Actions */}
          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              disabled={submitting}
              className="flex-1 px-6 py-2 bg-gray-800 border border-gray-700 rounded hover:bg-gray-700 transition-colors disabled:opacity-50 text-white"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="flex-1 px-6 py-2 bg-white text-black rounded font-semibold hover:bg-gray-200 transition-colors disabled:opacity-50"
            >
              {submitting ? 'Processing...' : 'Submit Evidence'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
