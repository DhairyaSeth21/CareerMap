type User = {
  id: number;
  name: string;
  email: string;
};

async function getUsers(): Promise<User[]> {
  try {
    const res = await fetch("http://localhost:8080/api/users", {
      // always hit the latest backend data
      cache: "no-store",
    });

    if (!res.ok) {
      console.error("Backend /api/users failed:", res.status);
      return [];
    }

    return await res.json();
  } catch (err) {
    console.error("Error fetching users:", err);
    return [];
  }
}

export default async function UsersPage() {
  const users = await getUsers();

  return (
    <div className="space-y-6">
      <header className="space-y-2">
        <h1 className="text-2xl md:text-3xl font-semibold tracking-tight">
          <span className="bg-gradient-to-r from-amber-400 via-orange-500 to-rose-500 bg-clip-text text-transparent">
            Users
          </span>
        </h1>
        <p className="text-sm text-slate-300/90">
          Right now this is seeded test data from your MySQL `users` table. In
          future: auth, multiple profiles, and personalized graphs.
        </p>
      </header>

      <div className="rounded-3xl border border-white/10 bg-black/40 p-6 backdrop-blur-2xl shadow-[0_0_40px_rgba(0,0,0,0.8)]">
        <div className="mb-4 flex items-center justify-between">
          <p className="text-sm text-slate-300">
            Total users:{" "}
            <span className="font-semibold text-amber-300">{users.length}</span>
          </p>
        </div>

        <div className="overflow-hidden rounded-2xl border border-white/10 bg-white/5">
          <table className="min-w-full text-left text-sm">
            <thead className="bg-gradient-to-r from-amber-400/20 via-orange-500/20 to-rose-500/20">
              <tr className="text-xs uppercase tracking-wide text-amber-100/90">
                <th className="px-6 py-3">ID</th>
                <th className="px-6 py-3">Name</th>
                <th className="px-6 py-3">Email</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/5">
              {users.length === 0 && (
                <tr>
                  <td
                    colSpan={3}
                    className="px-6 py-6 text-center text-sm text-slate-400"
                  >
                    No users yet. Make sure your Spring Boot backend is running
                    on <code className="text-amber-300">localhost:8080</code>.
                  </td>
                </tr>
              )}

              {users.map((u) => (
                <tr
                  key={u.id}
                  className="hover:bg-white/5 transition-colors"
                >
                  <td className="px-6 py-3 text-slate-300/90">{u.id}</td>
                  <td className="px-6 py-3 font-medium text-slate-100">
                    {u.name}
                  </td>
                  <td className="px-6 py-3 text-slate-300/90">{u.email}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
