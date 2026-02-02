'use client';

import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Play, FileText, Book, Star, ThumbsUp, RefreshCw, ExternalLink, Clock, Award } from 'lucide-react';
import { API_URL } from '../../config/api';

interface LearningResource {
  resourceId: number;
  title: string;
  url: string;
  type: string; // video, article, documentation, course, interactive
  source: string;
  description: string;
  estimatedMinutes: number;
  avgQualityScore: number;
  totalRatings: number;
  personalizedScore?: number;
  userRating?: number;
}

interface NodeResourcesProps {
  nodeId: number;
  userId: number;
  nodeName: string;
}

export default function NodeResources({ nodeId, userId, nodeName }: NodeResourcesProps) {
  const [resources, setResources] = useState<LearningResource[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [ratingResource, setRatingResource] = useState<number | null>(null);
  const [hoveredRating, setHoveredRating] = useState<{ [key: number]: number }>({});

  // Fetch resources on mount
  useEffect(() => {
    fetchResources();
  }, [nodeId, userId]);

  const fetchResources = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await fetch(`${API_URL}/api/resources/node/${nodeId}?userId=${userId}`);
      const data = await response.json();

      if (data.success) {
        setResources(data.resources);
      } else {
        setError(data.error || 'Failed to load resources');
      }
    } catch (err) {
      console.error('[RESOURCES] Error fetching:', err);
      setError('Failed to connect to server');
    } finally {
      setLoading(false);
    }
  };

  const rateResource = async (resourceId: number, rating: number, helpful: boolean) => {
    try {
      setRatingResource(resourceId);

      const response = await fetch(`${API_URL}/api/resources/rate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId,
          resourceId,
          rating,
          helpful
        })
      });

      const data = await response.json();

      if (data.success) {
        // Update local state to show user rating
        setResources(prev =>
          prev.map(r =>
            r.resourceId === resourceId ? { ...r, userRating: rating } : r
          )
        );
      }
    } catch (err) {
      console.error('[RESOURCES] Error rating:', err);
    } finally {
      setRatingResource(null);
    }
  };

  const findDifferentResource = async (resourceId: number) => {
    try {
      setRatingResource(resourceId);

      const response = await fetch(`${API_URL}/api/resources/find-different`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId,
          nodeId,
          currentResourceId: resourceId
        })
      });

      const data = await response.json();

      if (data.success) {
        setResources(data.resources);
      }
    } catch (err) {
      console.error('[RESOURCES] Error finding different:', err);
    } finally {
      setRatingResource(null);
    }
  };

  const getResourceIcon = (type: string) => {
    switch (type) {
      case 'video':
        return <Play className="w-4 h-4 text-purple-400" />;
      case 'article':
        return <FileText className="w-4 h-4 text-blue-400" />;
      case 'documentation':
        return <Book className="w-4 h-4 text-green-400" />;
      case 'course':
        return <Award className="w-4 h-4 text-yellow-400" />;
      case 'interactive':
        return <ExternalLink className="w-4 h-4 text-cyan-400" />;
      default:
        return <FileText className="w-4 h-4 text-slate-400" />;
    }
  };

  const getTypeColor = (type: string) => {
    switch (type) {
      case 'video':
        return 'bg-purple-500/20 text-purple-300 border-purple-500/30';
      case 'article':
        return 'bg-blue-500/20 text-blue-300 border-blue-500/30';
      case 'documentation':
        return 'bg-green-500/20 text-green-300 border-green-500/30';
      case 'course':
        return 'bg-yellow-500/20 text-yellow-300 border-yellow-500/30';
      case 'interactive':
        return 'bg-cyan-500/20 text-cyan-300 border-cyan-500/30';
      default:
        return 'bg-slate-500/20 text-slate-300 border-slate-500/30';
    }
  };

  if (loading) {
    return (
      <div className="space-y-4">
        <h3 className="text-white font-bold text-lg">Learning Resources</h3>
        <div className="space-y-3">
          {[1, 2].map(i => (
            <div
              key={i}
              className="bg-slate-800/50 border border-slate-700 rounded-lg p-4 animate-pulse"
            >
              <div className="h-4 bg-slate-700 rounded w-3/4 mb-2"></div>
              <div className="h-3 bg-slate-700 rounded w-full mb-2"></div>
              <div className="h-3 bg-slate-700 rounded w-2/3"></div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-500/10 border border-red-500/30 rounded-lg p-4">
        <p className="text-red-400 text-sm">{error}</p>
      </div>
    );
  }

  if (resources.length === 0) {
    return (
      <div className="bg-slate-800/50 border border-slate-700 rounded-lg p-4">
        <p className="text-slate-400 text-sm">No resources available yet. Resources will be discovered automatically as you progress.</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-white font-bold text-lg">Learning Resources</h3>
        <div className="flex items-center gap-1 text-xs text-slate-400">
          <Star className="w-3 h-3" />
          <span>Personalized for you</span>
        </div>
      </div>

      <AnimatePresence mode="popLayout">
        {resources.map((resource, index) => (
          <motion.div
            key={resource.resourceId}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, x: -100 }}
            transition={{ delay: index * 0.1 }}
            className="bg-slate-800 border border-slate-700 hover:border-slate-600 rounded-lg p-4 transition-all hover:shadow-lg hover:shadow-purple-500/10"
          >
            {/* Header */}
            <div className="flex items-start justify-between mb-3">
              <div className="flex items-center gap-2 flex-1">
                {getResourceIcon(resource.type)}
                <div className="flex flex-wrap items-center gap-2">
                  <span className={`px-2 py-0.5 rounded text-xs font-medium border ${getTypeColor(resource.type)}`}>
                    {resource.type}
                  </span>
                  <div className="flex items-center gap-1 text-xs text-slate-400">
                    <Clock className="w-3 h-3" />
                    <span>{resource.estimatedMinutes} min</span>
                  </div>
                </div>
              </div>

              {/* Quality Score */}
              <div className="flex items-center gap-1 bg-slate-700/50 px-2 py-1 rounded">
                <Star className="w-3 h-3 text-yellow-400 fill-yellow-400" />
                <span className="text-xs text-slate-300 font-medium">
                  {resource.avgQualityScore.toFixed(1)}
                </span>
                {resource.totalRatings > 0 && (
                  <span className="text-xs text-slate-500">
                    ({resource.totalRatings})
                  </span>
                )}
              </div>
            </div>

            {/* Title & Link */}
            <a
              href={resource.url}
              target="_blank"
              rel="noopener noreferrer"
              className="group block mb-2"
            >
              <h4 className="text-white font-semibold group-hover:text-purple-400 transition-colors flex items-center gap-2">
                {resource.title}
                <ExternalLink className="w-3 h-3 opacity-0 group-hover:opacity-100 transition-opacity" />
              </h4>
            </a>

            {/* Description */}
            <p className="text-slate-400 text-sm mb-3 line-clamp-2">
              {resource.description}
            </p>

            {/* Source Badge */}
            <div className="mb-3">
              <span className="inline-block px-2 py-1 bg-slate-700/50 text-slate-300 text-xs rounded capitalize">
                {resource.source}
              </span>
            </div>

            {/* User Actions */}
            <div className="flex items-center gap-2 pt-3 border-t border-slate-700">
              {/* This Helped Button */}
              <button
                onClick={() => rateResource(resource.resourceId, 5, true)}
                disabled={ratingResource === resource.resourceId}
                className="flex items-center gap-1.5 px-3 py-1.5 bg-green-600/20 hover:bg-green-600/40 text-green-400 rounded text-sm font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <ThumbsUp className="w-3.5 h-3.5" />
                This helped
              </button>

              {/* Find Different Button */}
              <button
                onClick={() => findDifferentResource(resource.resourceId)}
                disabled={ratingResource === resource.resourceId}
                className="flex items-center gap-1.5 px-3 py-1.5 bg-slate-700 hover:bg-slate-600 text-slate-300 rounded text-sm font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <RefreshCw className={`w-3.5 h-3.5 ${ratingResource === resource.resourceId ? 'animate-spin' : ''}`} />
                Find different
              </button>

              {/* Star Rating */}
              <div className="ml-auto flex items-center gap-0.5">
                {[1, 2, 3, 4, 5].map(star => (
                  <button
                    key={star}
                    onClick={() => rateResource(resource.resourceId, star, star >= 3)}
                    onMouseEnter={() => setHoveredRating(prev => ({ ...prev, [resource.resourceId]: star }))}
                    onMouseLeave={() => setHoveredRating(prev => ({ ...prev, [resource.resourceId]: 0 }))}
                    disabled={ratingResource === resource.resourceId}
                    className="transition-transform hover:scale-110 disabled:cursor-not-allowed"
                  >
                    <Star
                      className={`w-4 h-4 transition-colors ${
                        (hoveredRating[resource.resourceId] || resource.userRating || 0) >= star
                          ? 'text-yellow-400 fill-yellow-400'
                          : 'text-slate-600'
                      }`}
                    />
                  </button>
                ))}
              </div>
            </div>

            {/* User Rating Indicator */}
            {resource.userRating && (
              <div className="mt-2 pt-2 border-t border-slate-700">
                <p className="text-xs text-green-400 flex items-center gap-1">
                  <ThumbsUp className="w-3 h-3" />
                  You rated this {resource.userRating} stars
                </p>
              </div>
            )}
          </motion.div>
        ))}
      </AnimatePresence>
    </div>
  );
}
