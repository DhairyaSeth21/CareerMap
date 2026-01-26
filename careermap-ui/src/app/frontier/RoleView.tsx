import { motion } from 'framer-motion';
import { CareerRole, Domain } from './types';
import { ArrowLeft, Target } from 'lucide-react';

interface RoleViewProps {
  domain: Domain;
  roles: CareerRole[];
  onSelectRole: (role: CareerRole) => void;
  onBack: () => void;
}

/**
 * RoleView - Level 1 Zoom: Role Constellation within Domain
 * Displays roles as stars within the selected domain
 */
export default function RoleView({ domain, roles, onSelectRole, onBack }: RoleViewProps) {
  return (
    <div className="relative w-full h-screen flex items-center justify-center overflow-hidden">
      {/* Background with domain color */}
      <div className="absolute inset-0 bg-gradient-to-b from-slate-950 via-indigo-900 to-slate-950">
        {/* Ambient particles */}
        {[...Array(30)].map((_, i) => (
          <motion.div
            key={i}
            className="absolute w-2 h-2 bg-indigo-400 rounded-full blur-sm"
            style={{
              top: `${Math.random() * 100}%`,
              left: `${Math.random() * 100}%`,
            }}
            animate={{
              opacity: [0.3, 0.7, 0.3],
              scale: [1, 1.5, 1],
            }}
            transition={{
              duration: 3 + Math.random() * 2,
              repeat: Infinity,
              delay: Math.random() * 2,
            }}
          />
        ))}
      </div>

      {/* Back button */}
      <motion.button
        className="absolute top-8 left-8 flex items-center gap-2 text-slate-400 hover:text-white transition-colors"
        onClick={onBack}
        whileHover={{ x: -5 }}
      >
        <ArrowLeft className="w-5 h-5" />
        <span>Back to Domains</span>
      </motion.button>

      {/* Domain title */}
      <motion.div
        className="absolute top-20 left-1/2 transform -translate-x-1/2 text-center"
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
      >
        <h1 className="text-5xl font-bold bg-gradient-to-r from-indigo-300 to-purple-300 bg-clip-text text-transparent">
          {domain.name}
        </h1>
        <p className="text-slate-400 mt-4">Choose your role to generate your personalized path</p>
      </motion.div>

      {/* Role constellation */}
      <div className="relative w-full max-w-5xl mx-auto grid grid-cols-2 md:grid-cols-3 gap-8 p-12 mt-20">
        {roles.length === 0 ? (
          <motion.div
            className="col-span-full flex flex-col items-center justify-center py-20"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
          >
            <div className="text-center max-w-md">
              <div className="text-6xl mb-6">🚧</div>
              <h3 className="text-2xl font-bold text-white mb-3">
                No Roles Available Yet
              </h3>
              <p className="text-slate-400 mb-6">
                We're currently building out career paths for {domain.name}. Check back soon or explore other domains!
              </p>
              <button
                onClick={onBack}
                className="px-6 py-3 bg-purple-600 hover:bg-purple-700 text-white rounded-lg transition-colors"
              >
                Back to Domains
              </button>
            </div>
          </motion.div>
        ) : (
          roles.map((role, index) => (
            <motion.button
              key={role.careerRoleId}
              className="relative group"
              initial={{ opacity: 0, scale: 0, rotate: -180 }}
              animate={{ opacity: 1, scale: 1, rotate: 0 }}
              transition={{
                duration: 0.6,
                delay: index * 0.15,
                type: 'spring',
                stiffness: 100,
              }}
              whileHover={{ scale: 1.05, rotate: 5 }}
              onClick={() => onSelectRole(role)}
            >
              {/* Outer glow */}
              <div className="absolute inset-0 bg-gradient-to-r from-purple-500 to-indigo-500 rounded-xl blur-lg opacity-0 group-hover:opacity-50 transition-opacity duration-300" />

              {/* Role card */}
              <div className="relative bg-gradient-to-br from-slate-800 via-slate-850 to-slate-900 border border-slate-700 rounded-xl p-6 hover:border-purple-500 transition-all duration-300">
                {/* Icon */}
                <div className="flex justify-center mb-4">
                  <div className="w-16 h-16 bg-gradient-to-br from-purple-500 to-indigo-600 rounded-full flex items-center justify-center">
                    <Target className="w-8 h-8 text-white" />
                  </div>
                </div>

                {/* Name */}
                <h3 className="text-xl font-bold text-white mb-2 text-center">
                  {role.name}
                </h3>

                {/* Description */}
                <p className="text-slate-400 text-sm text-center line-clamp-3">
                  {role.description}
                </p>

                {/* Star indicator */}
                <div className="absolute top-2 right-2 flex gap-1">
                  {[...Array(3)].map((_, i) => (
                    <motion.div
                      key={i}
                      className="w-2 h-2 bg-purple-400 rounded-full"
                      animate={{
                        opacity: [0.4, 1, 0.4],
                      }}
                      transition={{
                        duration: 1.5,
                        repeat: Infinity,
                        delay: i * 0.3,
                      }}
                    />
                  ))}
                </div>
              </div>
            </motion.button>
          ))
        )}
      </div>
    </div>
  );
}
